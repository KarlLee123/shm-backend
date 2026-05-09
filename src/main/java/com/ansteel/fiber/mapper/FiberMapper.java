package com.ansteel.fiber.mapper;

import com.ansteel.fiber.dto.FiberRawUploadDTO;
import com.ansteel.fiber.vo.FiberRawVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FiberMapper {
    int insertRawData(FiberRawUploadDTO dto);
    FiberRawVO selectLatestRaw(@Param("sensorId") String sensorId);
    List<FiberRawVO> selectHistoryRaw(@Param("sensorId") String sensorId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime,
                                      @Param("limit") Integer limit);
}
