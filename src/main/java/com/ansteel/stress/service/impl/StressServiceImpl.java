package com.ansteel.stress.service.impl;

import com.ansteel.common.BusinessException;
import com.ansteel.stress.dto.StressQueryDTO;
import com.ansteel.stress.dto.StressRawUploadDTO;
import com.ansteel.stress.dto.StressResultUploadDTO;
import com.ansteel.stress.dto.StressVerifyDTO;
import com.ansteel.stress.entity.StressDataEntity;
import com.ansteel.stress.mapper.StressMapper;
import com.ansteel.stress.service.StressService;
import com.ansteel.stress.vo.StressMonitorVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Service
public class StressServiceImpl implements StressService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 500;

    private final StressMapper stressMapper;

    public StressServiceImpl(StressMapper stressMapper) {
        this.stressMapper = stressMapper;
    }

    /**
     * 原始数据上传：
     * 仅负责原始输入入库，不负责计算与核验。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadRawData(StressRawUploadDTO dto) {
        validateRawUploadDTO(dto);

        String sensorId = dto.getSensorId().trim();
        String collectTime = normalizeDateTimeString(dto.getCollectTime(), "collectTime");

        dto.setSensorId(sensorId);
        dto.setCollectTime(collectTime);

        try {
            int rows = stressMapper.insertRawData(dto);
            if (rows != 1) {
                throw new BusinessException("原始数据入库失败");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一sensorId与collectTime的原始记录已存在");
        }
    }

    /**
     * Python 结果回写：
     * 只允许 RAW / REJECTED -> CALCULATED
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadResultData(StressResultUploadDTO dto) {
        validateResultUploadDTO(dto);

        String sensorId = dto.getSensorId().trim();
        String collectTimeStr = normalizeDateTimeString(dto.getCollectTime(), "collectTime");
        LocalDateTime collectTime = parseDateTime(collectTimeStr, "collectTime");

        String calcTimeStr = hasText(dto.getCalcTime())
                ? normalizeDateTimeString(dto.getCalcTime(), "calcTime")
                : LocalDateTime.now().format(DATE_TIME_FORMATTER);

        StressDataEntity record = stressMapper.selectBySensorIdAndCollectTime(sensorId, collectTime);
        if (record == null) {
            throw new BusinessException("未找到对应原始记录");
        }

        Integer verifyStatus = record.getVerifyStatus();
        if (verifyStatus != null && verifyStatus == 2) {
            throw new BusinessException("已核验通过的数据禁止再次回写结果");
        }
        if (verifyStatus != null && verifyStatus == 1) {
            throw new BusinessException("该记录已存在待核验结果，无需重复回写");
        }
        if (verifyStatus != null && verifyStatus != 0 && verifyStatus != 3) {
            throw new BusinessException("当前记录状态不允许结果回写");
        }

        dto.setSensorId(sensorId);
        dto.setCollectTime(collectTimeStr);
        dto.setCalcTime(calcTimeStr);

        int rows = stressMapper.updateResultData(dto);
        if (rows != 1) {
            throw new BusinessException("结果回写失败");
        }
    }

    /**
     * 人工核验：
     * 只允许 CALCULATED -> VERIFIED / REJECTED
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyStress(StressVerifyDTO dto) {
        validateVerifyDTO(dto);

        String sensorId = dto.getSensorId().trim();
        String action = dto.getAction().trim().toUpperCase(Locale.ROOT);
        String collectTimeStr = normalizeDateTimeString(dto.getCollectTime(), "collectTime");
        LocalDateTime collectTime = parseDateTime(collectTimeStr, "collectTime");

        String verifyTimeStr = hasText(dto.getVerifyTime())
                ? normalizeDateTimeString(dto.getVerifyTime(), "verifyTime")
                : LocalDateTime.now().format(DATE_TIME_FORMATTER);

        StressDataEntity record = stressMapper.selectBySensorIdAndCollectTime(sensorId, collectTime);
        if (record == null) {
            throw new BusinessException("未找到对应记录");
        }

        Integer verifyStatus = record.getVerifyStatus();
        if (verifyStatus != null && verifyStatus == 0) {
            throw new BusinessException("RAW 状态数据不可直接核验");
        }
        if (verifyStatus != null && verifyStatus == 2) {
            throw new BusinessException("该记录已核验通过，无需重复核验");
        }
        if (verifyStatus != null && verifyStatus == 3) {
            throw new BusinessException("REJECTED 状态需重新计算后再核验");
        }
        if (verifyStatus == null || verifyStatus != 1) {
            throw new BusinessException("当前记录状态不允许核验");
        }

        dto.setSensorId(sensorId);
        dto.setAction(action);
        dto.setCollectTime(collectTimeStr);
        dto.setVerifyTime(verifyTimeStr);

        if ("PASS".equals(action)) {
            int rows = stressMapper.passVerify(dto);
            if (rows != 1) {
                throw new BusinessException("核验通过操作失败");
            }
            return;
        }

        int rows = stressMapper.failVerify(dto);
        if (rows != 1) {
            throw new BusinessException("核验驳回操作失败");
        }
    }

    /**
     * 查询最新一条业务可见数据：
     * 默认只返回 VERIFIED
     */
    @Override
    public StressMonitorVO getLatest(StressQueryDTO dto) {
        validateQueryDTO(dto);

        String sensorId = dto.getSensorId().trim();
        return stressMapper.selectLatestVerified(sensorId);
    }

    /**
     * 查询历史业务可见数据：
     * 默认只返回 VERIFIED
     */
    @Override
    public List<StressMonitorVO> getHistory(StressQueryDTO dto) {
        validateQueryDTO(dto);

        String sensorId = dto.getSensorId().trim();
        LocalDateTime startTime = parseNullableDateTime(dto.getStartTime(), "startTime");
        LocalDateTime endTime = parseNullableDateTime(dto.getEndTime(), "endTime");
        Integer limit = normalizeLimit(dto.getLimit());

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BusinessException("startTime 不能晚于 endTime");
        }

        return stressMapper.selectHistoryVerified(sensorId, startTime, endTime, limit);
    }

    private void validateRawUploadDTO(StressRawUploadDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求体不能为空");
        }
        if (!hasText(dto.getSensorId())) {
            throw new BusinessException("sensorId 不能为空");
        }
        if (dto.getForceValue() == null) {
            throw new BusinessException("forceValue 不能为空");
        }
        if (dto.getDisplacementValue() == null) {
            throw new BusinessException("displacementValue 不能为空");
        }
        if (!hasText(dto.getCollectTime())) {
            throw new BusinessException("collectTime 不能为空");
        }

        validateDecimal(dto.getForceValue(), "forceValue");
        validateDecimal(dto.getDisplacementValue(), "displacementValue");
        normalizeDateTimeString(dto.getCollectTime(), "collectTime");
    }

    private void validateResultUploadDTO(StressResultUploadDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求体不能为空");
        }
        if (!hasText(dto.getSensorId())) {
            throw new BusinessException("sensorId 不能为空");
        }
        if (dto.getCalcStress() == null) {
            throw new BusinessException("calcStress 不能为空");
        }
        if (!hasText(dto.getCollectTime())) {
            throw new BusinessException("collectTime 不能为空");
        }

        validateDecimal(dto.getCalcStress(), "calcStress");
        normalizeDateTimeString(dto.getCollectTime(), "collectTime");

        if (hasText(dto.getCalcTime())) {
            normalizeDateTimeString(dto.getCalcTime(), "calcTime");
        }
    }

    private void validateVerifyDTO(StressVerifyDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求体不能为空");
        }
        if (!hasText(dto.getSensorId())) {
            throw new BusinessException("sensorId 不能为空");
        }
        if (!hasText(dto.getAction())) {
            throw new BusinessException("action 不能为空");
        }
        if (!hasText(dto.getCollectTime())) {
            throw new BusinessException("collectTime 不能为空");
        }

        String action = dto.getAction().trim().toUpperCase(Locale.ROOT);
        if (!"PASS".equals(action) && !"FAIL".equals(action)) {
            throw new BusinessException("action 只能为 PASS 或 FAIL");
        }

        normalizeDateTimeString(dto.getCollectTime(), "collectTime");

        if (hasText(dto.getVerifyTime())) {
            normalizeDateTimeString(dto.getVerifyTime(), "verifyTime");
        }

        if ("PASS".equals(action)) {
            if (dto.getVerifiedStress() == null) {
                throw new BusinessException("PASS 时 verifiedStress 不能为空");
            }
            validateDecimal(dto.getVerifiedStress(), "verifiedStress");
        }

        if ("FAIL".equals(action)) {
            if (!hasText(dto.getVerifyRemark())) {
                throw new BusinessException("FAIL 时 verifyRemark 不能为空");
            }
        }
    }

    private void validateQueryDTO(StressQueryDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求体不能为空");
        }
        if (!hasText(dto.getSensorId())) {
            throw new BusinessException("sensorId 不能为空");
        }

        if (hasText(dto.getStartTime())) {
            normalizeDateTimeString(dto.getStartTime(), "startTime");
        }
        if (hasText(dto.getEndTime())) {
            normalizeDateTimeString(dto.getEndTime(), "endTime");
        }
    }

    private void validateDecimal(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new BusinessException(fieldName + " 不能为空");
        }
    }

    private LocalDateTime parseNullableDateTime(String value, String fieldName) {
        if (!hasText(value)) {
            return null;
        }
        return parseDateTime(value.trim(), fieldName);
    }

    private LocalDateTime parseDateTime(String value, String fieldName) {
        try {
            return LocalDateTime.parse(value.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException(fieldName + " 格式错误，必须为 yyyy-MM-dd HH:mm:ss");
        }
    }

    private String normalizeDateTimeString(String value, String fieldName) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(value.trim(), DATE_TIME_FORMATTER);
            return dateTime.format(DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException(fieldName + " 格式错误，必须为 yyyy-MM-dd HH:mm:ss");
        }
    }

    private Integer normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}