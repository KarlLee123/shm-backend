package com.ansteel.acceleration.service;

import com.ansteel.acceleration.dto.AccelerationQueryDTO;
import com.ansteel.acceleration.dto.AccelerationRawUploadDTO;
import com.ansteel.acceleration.vo.AccelerationRawVO;

import java.util.List;

public interface AccelerationService {

    void uploadRawData(AccelerationRawUploadDTO dto);

    AccelerationRawVO getLatestRaw(AccelerationQueryDTO dto);

    List<AccelerationRawVO> getHistoryRaw(AccelerationQueryDTO dto);
}