package com.ansteel.vibrationdat.service;

import com.ansteel.vibrationdat.dto.VibrationDatQueryDTO;
import com.ansteel.vibrationdat.dto.VibrationDatUploadDTO;
import com.ansteel.vibrationdat.vo.VibrationDatVO;
import java.util.List;

public interface VibrationDatService {
    void uploadRawData(VibrationDatUploadDTO dto);
    VibrationDatVO getLatestRaw(VibrationDatQueryDTO dto);
    List<VibrationDatVO> getHistoryRaw(VibrationDatQueryDTO dto);
}
