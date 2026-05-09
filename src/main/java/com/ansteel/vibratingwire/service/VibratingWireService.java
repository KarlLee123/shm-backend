package com.ansteel.vibratingwire.service;

import com.ansteel.vibratingwire.dto.VibratingWireQueryDTO;
import com.ansteel.vibratingwire.dto.VibratingWireRawUploadDTO;
import com.ansteel.vibratingwire.vo.VibratingWireRawVO;
import java.util.List;

public interface VibratingWireService {
    void uploadRawData(VibratingWireRawUploadDTO dto);
    VibratingWireRawVO getLatestRaw(VibratingWireQueryDTO dto);
    List<VibratingWireRawVO> getHistoryRaw(VibratingWireQueryDTO dto);
}
