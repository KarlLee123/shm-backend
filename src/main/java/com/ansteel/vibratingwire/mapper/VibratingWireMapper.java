package com.ansteel.vibratingwire.mapper;

import com.ansteel.vibratingwire.dto.VibratingWireRawUploadDTO;
import com.ansteel.vibratingwire.vo.VibratingWireRawVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VibratingWireMapper {
    int insertRawData(VibratingWireRawUploadDTO dto);
    VibratingWireRawVO selectLatestRaw(@Param("sensorId") String sensorId);
    List<VibratingWireRawVO> selectHistoryRaw(@Param("sensorId") String sensorId,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime,
                                              @Param("limit") Integer limit);
}
