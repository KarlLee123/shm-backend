package com.ansteel.vibration.mapper;

import com.ansteel.vibration.dto.VibrationRawUploadDTO;
import com.ansteel.vibration.vo.VibrationRawVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VibrationMapper {

    int insertRawData(VibrationRawUploadDTO dto);

    VibrationRawVO selectLatestRaw(@Param("sensorId") String sensorId);

    List<VibrationRawVO> selectHistoryRaw(@Param("sensorId") String sensorId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("limit") Integer limit);
}