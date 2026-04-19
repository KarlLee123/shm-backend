package com.ansteel.vibration.service;

import com.ansteel.vibration.dto.VibrationQueryDTO;
import com.ansteel.vibration.dto.VibrationRawUploadDTO;
import com.ansteel.vibration.vo.VibrationRawVO;

import java.util.List;

public interface VibrationService {

    void uploadRawData(VibrationRawUploadDTO dto);

    VibrationRawVO getLatestRaw(VibrationQueryDTO dto);

    List<VibrationRawVO> getHistoryRaw(VibrationQueryDTO dto);
}