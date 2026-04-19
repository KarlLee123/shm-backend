package com.ansteel.stress.service;

import com.ansteel.stress.dto.StressQueryDTO;
import com.ansteel.stress.dto.StressRawUploadDTO;
import com.ansteel.stress.dto.StressResultUploadDTO;
import com.ansteel.stress.dto.StressVerifyDTO;
import com.ansteel.stress.vo.StressMonitorVO;

import java.util.List;

public interface StressService {

    /**
     * 原始数据上传
     */
    void uploadRawData(StressRawUploadDTO dto);

    /**
     * Python 结果回写
     */
    void uploadResultData(StressResultUploadDTO dto);

    /**
     * 人工核验
     */
    void verifyStress(StressVerifyDTO dto);

    /**
     * 查询最新一条业务可见数据
     */
    StressMonitorVO getLatest(StressQueryDTO dto);

    /**
     * 查询历史业务可见数据
     */
    List<StressMonitorVO> getHistory(StressQueryDTO dto);
}