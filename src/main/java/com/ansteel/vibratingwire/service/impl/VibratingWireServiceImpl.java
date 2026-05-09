package com.ansteel.vibratingwire.service.impl;

import com.ansteel.common.BusinessException;
import com.ansteel.vibratingwire.dto.VibratingWireQueryDTO;
import com.ansteel.vibratingwire.dto.VibratingWireRawUploadDTO;
import com.ansteel.vibratingwire.mapper.VibratingWireMapper;
import com.ansteel.vibratingwire.service.VibratingWireService;
import com.ansteel.vibratingwire.vo.VibratingWireRawVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class VibratingWireServiceImpl implements VibratingWireService {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 500;
    private final VibratingWireMapper mapper;
    public VibratingWireServiceImpl(VibratingWireMapper mapper) { this.mapper = mapper; }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadRawData(VibratingWireRawUploadDTO dto) {
        validateRaw(dto);
        dto.setSensorId(dto.getSensorId().trim());
        dto.setCollectTime(normalize(dto.getCollectTime(), "collectTime"));
        try {
            if (mapper.insertRawData(dto) != 1) throw new BusinessException("振弦原始数据入库失败");
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一sensorId与collectTime的振弦原始记录已存在");
        }
    }
    @Override
    public VibratingWireRawVO getLatestRaw(VibratingWireQueryDTO dto) { validateQuery(dto); return mapper.selectLatestRaw(dto.getSensorId().trim()); }
    @Override
    public List<VibratingWireRawVO> getHistoryRaw(VibratingWireQueryDTO dto) {
        validateQuery(dto);
        LocalDateTime start = parseNullable(dto.getStartTime(), "startTime");
        LocalDateTime end = parseNullable(dto.getEndTime(), "endTime");
        if (start != null && end != null && start.isAfter(end)) throw new BusinessException("startTime 不能晚于 endTime");
        return mapper.selectHistoryRaw(dto.getSensorId().trim(), start, end, normalizeLimit(dto.getLimit()));
    }
    private void validateRaw(VibratingWireRawUploadDTO dto) {
        if (dto == null) throw new BusinessException("请求体不能为空");
        if (!hasText(dto.getSensorId())) throw new BusinessException("sensorId 不能为空");
        if (!hasText(dto.getDeviceNo())) throw new BusinessException("deviceNo 不能为空");
        if (!hasText(dto.getChannelNo())) throw new BusinessException("channelNo 不能为空");
        if (dto.getFrequency() == null) throw new BusinessException("frequency 不能为空");
        if (dto.getTemperature() == null) throw new BusinessException("temperature 不能为空");
        if (!hasText(dto.getCollectTime())) throw new BusinessException("collectTime 不能为空");
        validateDecimal(dto.getFrequency(), "frequency");
        validateDecimal(dto.getTemperature(), "temperature");
        if (dto.getTension() != null) validateDecimal(dto.getTension(), "tension");
        if (dto.getStrainValue() != null) validateDecimal(dto.getStrainValue(), "strainValue");
        normalize(dto.getCollectTime(), "collectTime");
    }
    private void validateQuery(VibratingWireQueryDTO dto) {
        if (dto == null) throw new BusinessException("请求体不能为空");
        if (!hasText(dto.getSensorId())) throw new BusinessException("sensorId 不能为空");
        if (hasText(dto.getStartTime())) normalize(dto.getStartTime(), "startTime");
        if (hasText(dto.getEndTime())) normalize(dto.getEndTime(), "endTime");
    }
    private void validateDecimal(BigDecimal value, String field) { if (value == null) throw new BusinessException(field + " 不能为空"); }
    private LocalDateTime parseNullable(String value, String field) { if (!hasText(value)) return null; try { return LocalDateTime.parse(value.trim(), FMT); } catch (DateTimeParseException e) { throw new BusinessException(field + " 格式错误，必须为 yyyy-MM-dd HH:mm:ss"); } }
    private String normalize(String value, String field) { try { return LocalDateTime.parse(value.trim(), FMT).format(FMT); } catch (DateTimeParseException e) { throw new BusinessException(field + " 格式错误，必须为 yyyy-MM-dd HH:mm:ss"); } }
    private Integer normalizeLimit(Integer limit) { return limit == null || limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT); }
    private boolean hasText(String value) { return value != null && !value.trim().isEmpty(); }
}
