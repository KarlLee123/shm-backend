package com.ansteel.fiber.service.impl;

import com.ansteel.common.BusinessException;
import com.ansteel.fiber.dto.FiberQueryDTO;
import com.ansteel.fiber.dto.FiberRawUploadDTO;
import com.ansteel.fiber.mapper.FiberMapper;
import com.ansteel.fiber.service.FiberService;
import com.ansteel.fiber.vo.FiberRawVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class FiberServiceImpl implements FiberService {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 500;
    private final FiberMapper fiberMapper;

    public FiberServiceImpl(FiberMapper fiberMapper) {
        this.fiberMapper = fiberMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadRawData(FiberRawUploadDTO dto) {
        validateRaw(dto);
        dto.setSensorId(dto.getSensorId().trim());
        dto.setCollectTime(normalize(dto.getCollectTime(), "collectTime"));
        try {
            if (fiberMapper.insertRawData(dto) != 1) {
                throw new BusinessException("原始光纤数据入库失败");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一sensorId与collectTime的光纤原始记录已存在");
        }
    }

    @Override
    public FiberRawVO getLatestRaw(FiberQueryDTO dto) {
        validateQuery(dto);
        return fiberMapper.selectLatestRaw(dto.getSensorId().trim());
    }

    @Override
    public List<FiberRawVO> getHistoryRaw(FiberQueryDTO dto) {
        validateQuery(dto);
        LocalDateTime start = parseNullable(dto.getStartTime(), "startTime");
        LocalDateTime end = parseNullable(dto.getEndTime(), "endTime");
        if (start != null && end != null && start.isAfter(end)) {
            throw new BusinessException("startTime 不能晚于 endTime");
        }
        return fiberMapper.selectHistoryRaw(dto.getSensorId().trim(), start, end, normalizeLimit(dto.getLimit()));
    }

    private void validateRaw(FiberRawUploadDTO dto) {
        if (dto == null) throw new BusinessException("请求体不能为空");
        if (!hasText(dto.getSensorId())) throw new BusinessException("sensorId 不能为空");
        if (!hasText(dto.getDeviceNo())) throw new BusinessException("deviceNo 不能为空");
        if (!hasText(dto.getFiberNo())) throw new BusinessException("fiberNo 不能为空");
        if (dto.getRawValue() == null) throw new BusinessException("rawValue 不能为空");
        if (dto.getWavelength() == null) throw new BusinessException("wavelength 不能为空");
        if (dto.getWavelengthShift() == null) throw new BusinessException("wavelengthShift 不能为空");
        if (dto.getIntensity() == null) throw new BusinessException("intensity 不能为空");
        if (!hasText(dto.getCollectTime())) throw new BusinessException("collectTime 不能为空");
        validateDecimal(dto.getRawValue(), "rawValue");
        validateDecimal(dto.getWavelength(), "wavelength");
        validateDecimal(dto.getWavelengthShift(), "wavelengthShift");
        validateDecimal(dto.getIntensity(), "intensity");
        normalize(dto.getCollectTime(), "collectTime");
    }

    private void validateQuery(FiberQueryDTO dto) {
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
        try { return LocalDateTime.parse(value.trim(), FMT); }
        catch (DateTimeParseException e) { throw new BusinessException(field + " 格式错误，必须为 yyyy-MM-dd HH:mm:ss"); }
    }

    private String normalize(String value, String field) {
        try { return LocalDateTime.parse(value.trim(), FMT).format(FMT); }
        catch (DateTimeParseException e) { throw new BusinessException(field + " 格式错误，必须为 yyyy-MM-dd HH:mm:ss"); }
    }

    private Integer normalizeLimit(Integer limit) { return limit == null || limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT); }
    private boolean hasText(String value) { return value != null && !value.trim().isEmpty(); }
}
