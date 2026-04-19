package com.ansteel.displacement.service;

import com.ansteel.displacement.dto.DisplacementQueryDTO;
import com.ansteel.displacement.dto.DisplacementRawUploadDTO;
import com.ansteel.displacement.vo.DisplacementRawVO;

import java.util.List;

public interface DisplacementService {

    /**
     * 原始位移数据上传
     */
    void uploadRawData(DisplacementRawUploadDTO dto);

    /**
     * 查询最新一条原始位移数据
     */
    DisplacementRawVO getLatestRaw(DisplacementQueryDTO dto);

    /**
     * 查询历史原始位移数据
     */
    List<DisplacementRawVO> getHistoryRaw(DisplacementQueryDTO dto);
}