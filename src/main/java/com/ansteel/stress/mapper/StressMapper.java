package com.ansteel.stress.mapper;

import com.ansteel.stress.dto.StressRawUploadDTO;
import com.ansteel.stress.dto.StressResultUploadDTO;
import com.ansteel.stress.dto.StressVerifyDTO;
import com.ansteel.stress.entity.StressDataEntity;
import com.ansteel.stress.vo.StressMonitorVO;
import com.ansteel.stress.vo.StressRawVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StressMapper {

    /**
     * 原始数据入库
     */
    int insertRawData(StressRawUploadDTO dto);

    /**
     * 查询指定原始记录
     */
    StressDataEntity selectBySensorIdAndCollectTime(@Param("sensorId") String sensorId,
                                                    @Param("collectTime") LocalDateTime collectTime);

    /**
     * Python 结果回写
     */
    int updateResultData(StressResultUploadDTO dto);

    /**
     * 核验通过
     */
    int passVerify(StressVerifyDTO dto);

    /**
     * 核验打回
     */
    int failVerify(StressVerifyDTO dto);

    /**
     * 查询最新一条核验通过数据
     */
    StressMonitorVO selectLatestVerified(@Param("sensorId") String sensorId);

    /**
     * 查询历史核验通过数据
     */
    List<StressMonitorVO> selectHistoryVerified(@Param("sensorId") String sensorId,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime,
                                                @Param("limit") Integer limit);

    /**
     * 旧版：查询最新一条原始数据
     * 先保留，等 Service/Controller 完整切换后再删
     */
    StressRawVO selectLatestRaw(@Param("sensorId") String sensorId);

    /**
     * 旧版：查询历史原始数据
     * 先保留，等 Service/Controller 完整切换后再删
     */
    List<StressRawVO> selectHistoryRaw(@Param("sensorId") String sensorId,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime,
                                       @Param("limit") Integer limit);
}