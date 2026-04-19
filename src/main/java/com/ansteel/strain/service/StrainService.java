package com.ansteel.strain.service;

import com.ansteel.strain.dto.StrainQueryDTO;
import com.ansteel.strain.dto.StrainRawUploadDTO;
import com.ansteel.strain.vo.StrainRawVO;

import java.util.List;

public interface StrainService {

    void uploadRawData(StrainRawUploadDTO dto);

    StrainRawVO getLatestRaw(StrainQueryDTO dto);

    List<StrainRawVO> getHistoryRaw(StrainQueryDTO dto);
}