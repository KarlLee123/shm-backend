package com.ansteel.fiber.service;

import com.ansteel.fiber.dto.FiberQueryDTO;
import com.ansteel.fiber.dto.FiberRawUploadDTO;
import com.ansteel.fiber.vo.FiberRawVO;

import java.util.List;

public interface FiberService {
    void uploadRawData(FiberRawUploadDTO dto);
    FiberRawVO getLatestRaw(FiberQueryDTO dto);
    List<FiberRawVO> getHistoryRaw(FiberQueryDTO dto);
}
