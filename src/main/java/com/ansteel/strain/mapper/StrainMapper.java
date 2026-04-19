package com.ansteel.strain.mapper;

import com.ansteel.strain.dto.StrainRawUploadDTO;
import com.ansteel.strain.vo.StrainRawVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StrainMapper {

    int insertRawData(StrainRawUploadDTO dto);

    StrainRawVO selectLatestRaw(@Param("sensorId") String sensorId);

    List<StrainRawVO> selectHistoryRaw(@Param("sensorId") String sensorId,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime,
                                       @Param("limit") Integer limit);
}