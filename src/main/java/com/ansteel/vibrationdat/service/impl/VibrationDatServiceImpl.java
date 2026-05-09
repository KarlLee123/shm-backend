package com.ansteel.vibrationdat.service.impl;

import com.ansteel.common.BusinessException;
import com.ansteel.vibrationdat.dto.VibrationDatQueryDTO;
import com.ansteel.vibrationdat.dto.VibrationDatUploadDTO;
import com.ansteel.vibrationdat.mapper.VibrationDatMapper;
import com.ansteel.vibrationdat.service.VibrationDatService;
import com.ansteel.vibrationdat.vo.VibrationDatVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class VibrationDatServiceImpl implements VibrationDatService {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 500;
    private final VibrationDatMapper mapper;

    public VibrationDatServiceImpl(VibrationDatMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadRawData(VibrationDatUploadDTO dto) {
        validateRaw(dto);
        dto.setSensorId(dto.getSensorId().trim());
        dto.setCollectTime(normalize(dto.getCollectTime(), "collectTime"));
        try {
            if (mapper.insertRawData(dto) != 1) {
                throw new BusinessException("vibration DAT 原始文件入库失败");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一sensorId与collectTime的 vibration DAT 记录已存在");
        }
    }

    @Override
    public VibrationDatVO getLatestRaw(VibrationDatQueryDTO dto) {
        validateQuery(dto);
        return mapper.selectLatestRaw(dto.getSensorId().trim());
    }

    @Override
    public List<VibrationDatVO> getHistoryRaw(VibrationDatQueryDTO dto) {
        validateQuery(dto);
        LocalDateTime start = parseNullable(dto.getStartTime(), "startTime");
        LocalDateTime end = parseNullable(dto.getEndTime(), "endTime");
        if (start != null && end != null && start.isAfter(end)) {
            throw new BusinessException("startTime 不能晚于 endTime");
        }
        return mapper.selectHistoryRaw(dto.getSensorId().trim(), start, end, normalizeLimit(dto.getLimit()));
    }

    private void validateRaw(VibrationDatUploadDTO dto) {
        if (dto == null) throw new BusinessException("请求体不能为空");
        if (!hasText(dto.getSensorId())) throw new BusinessException("sensorId 不能为空");
        if (!hasText(dto.getDeviceNo())) throw new BusinessException("deviceNo 不能为空");
        if (!hasText(dto.getFileName())) throw new BusinessException("fileName 不能为空");
        if (!hasText(dto.getFilePath())) throw new BusinessException("filePath 不能为空");
        if (!hasText(dto.getCollectTime())) throw new BusinessException("collectTime 不能为空");
        if (dto.getFileSize() != null && dto.getFileSize() < 0) throw new BusinessException("fileSize 不能小于 0");
        if (dto.getSampleRate() != null) validateDecimal(dto.getSampleRate(), "sampleRate");
        if (dto.getChannelCount() != null && dto.getChannelCount() < 0) throw new BusinessException("channelCount 不能小于 0");
        if (dto.getPointCount() != null && dto.getPointCount() < 0) throw new BusinessException("pointCount 不能小于 0");
        if (dto.getDurationSeconds() != null) validateDecimal(dto.getDurationSeconds(), "durationSeconds");
        normalize(dto.getCollectTime(), "collectTime");
    }

    private void validateQuery(VibrationDatQueryDTO dto) {
        if (dto == null) throw new BusinessException("请求体不能为空");
        if (!hasText(dto.getSensorId())) throw new BusinessException("sensorId 不能为空");
        if (hasText(dto.getStartTime())) normalize(dto.getStartTime(), "startTime");
        if (hasText(dto.getEndTime())) normalize(dto.getEndTime(), "endTime");
    }

    private void validateDecimal(BigDecimal value, String field) {
        if (value == null) throw new BusinessException(field + " 不能为空");
    }

    private LocalDateTime parseNullable(String value, String field) {
        if (!hasText(value)) return null;
        try {
            return LocalDateTime.parse(value.trim(), FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(field + " 格式错误，必须为 yyyy-MM-dd HH:mm:ss");
        }
    }

    private String normalize(String value, String field) {
        try {
            return LocalDateTime.parse(value.trim(), FMT).format(FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(field + " 格式错误，必须为 yyyy-MM-dd HH:mm:ss");
        }
    }

    private Integer normalizeLimit(Integer limit) {
        return limit == null || limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
