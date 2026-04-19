package com.ansteel.acceleration.mapper;

import com.ansteel.acceleration.dto.AccelerationRawUploadDTO;
import com.ansteel.acceleration.vo.AccelerationRawVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AccelerationMapper {

    int insertRawData(AccelerationRawUploadDTO dto);

    AccelerationRawVO selectLatestRaw(@Param("sensorId") String sensorId);

    List<AccelerationRawVO> selectHistoryRaw(@Param("sensorId") String sensorId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("limit") Integer limit);
}