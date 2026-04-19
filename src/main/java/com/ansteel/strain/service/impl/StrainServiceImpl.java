package com.ansteel.strain.service.impl;

import com.ansteel.common.BusinessException;
import com.ansteel.strain.dto.StrainQueryDTO;
import com.ansteel.strain.dto.StrainRawUploadDTO;
import com.ansteel.strain.mapper.StrainMapper;
import com.ansteel.strain.service.StrainService;
import com.ansteel.strain.vo.StrainRawVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class StrainServiceImpl implements StrainService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 500;

    private final StrainMapper strainMapper;

    public StrainServiceImpl(StrainMapper strainMapper) {
        this.strainMapper = strainMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadRawData(StrainRawUploadDTO dto) {
        validateRawUploadDTO(dto);

        String sensorId = dto.getSensorId().trim();
        String collectTime = normalizeDateTimeString(dto.getCollectTime(), "collectTime");

        dto.setSensorId(sensorId);
        dto.setCollectTime(collectTime);

        try {
            int rows = strainMapper.insertRawData(dto);
            if (rows != 1) {
                throw new BusinessException("原始应变数据入库失败");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一sensorId与collectTime的原始记录已存在");
        }
    }

    @Override
    public StrainRawVO getLatestRaw(StrainQueryDTO dto) {
        validateLatestQueryDTO(dto);
        String sensorId = dto.getSensorId().trim();
        return strainMapper.selectLatestRaw(sensorId);
    }

    @Override
    public List<StrainRawVO> getHistoryRaw(StrainQueryDTO dto) {
        validateHistoryQueryDTO(dto);

        String sensorId = dto.getSensorId().trim();
        LocalDateTime startTime = parseNullableDateTime(dto.getStartTime(), "startTime");
        LocalDateTime endTime = parseNullableDateTime(dto.getEndTime(), "endTime");
        Integer limit = normalizeLimit(dto.getLimit());

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BusinessException("startTime 不能晚于 endTime");
        }

        return strainMapper.selectHistoryRaw(sensorId, startTime, endTime, limit);
    }

    private void validateRawUploadDTO(StrainRawUploadDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求体不能为空");
        }
        if (!hasText(dto.getSensorId())) {
            throw new BusinessException("sensorId 不能为空");
        }
        if (dto.getStrainValue() == null) {
            throw new BusinessException("strainValue 不能为空");
        }
        if (!hasText(dto.getCollectTime())) {
            throw new BusinessException("collectTime 不能为空");
        }

        validateDecimal(dto.getStrainValue(), "strainValue");
        normalizeDateTimeString(dto.getCollectTime(), "collectTime");
    }

    private void validateLatestQueryDTO(StrainQueryDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求体不能为空");
        }
        if (!hasText(dto.getSensorId())) {
            throw new BusinessException("sensorId 不能为空");
        }
    }

    private void validateHistoryQueryDTO(StrainQueryDTO dto) {
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