package com.ansteel.displacement.mapper;

import com.ansteel.displacement.dto.DisplacementRawUploadDTO;
import com.ansteel.displacement.vo.DisplacementRawVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DisplacementMapper {

    /**
     * 原始位移数据入库
     */
    int insertRawData(DisplacementRawUploadDTO dto);

    /**
     * 查询最新一条原始位移数据
     */
    DisplacementRawVO selectLatestRaw(@Param("sensorId") String sensorId);

    /**
     * 查询历史原始位移数据
     */
    List<DisplacementRawVO> selectHistoryRaw(@Param("sensorId") String sensorId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("limit") Integer limit);
}