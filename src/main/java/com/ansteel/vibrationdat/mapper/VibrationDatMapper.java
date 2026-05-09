package com.ansteel.vibrationdat.mapper;

import com.ansteel.vibrationdat.dto.VibrationDatUploadDTO;
import com.ansteel.vibrationdat.vo.VibrationDatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VibrationDatMapper {
    int insertRawData(VibrationDatUploadDTO dto);
    VibrationDatVO selectLatestRaw(@Param("sensorId") String sensorId);
    List<VibrationDatVO> selectHistoryRaw(@Param("sensorId") String sensorId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("limit") Integer limit);
}
