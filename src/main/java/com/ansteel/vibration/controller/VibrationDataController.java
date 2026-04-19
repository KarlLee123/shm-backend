package com.ansteel.vibration.controller;

import com.ansteel.common.Result;
import com.ansteel.vibration.dto.VibrationQueryDTO;
import com.ansteel.vibration.service.VibrationService;
import com.ansteel.vibration.vo.VibrationRawVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/vibration")
public class VibrationDataController {

    private final VibrationService vibrationService;

    public VibrationDataController(VibrationService vibrationService) {
        this.vibrationService = vibrationService;
    }

    @PostMapping("/latest")
    public Result<VibrationRawVO> getLatestRaw(@RequestBody VibrationQueryDTO dto) {
        return Result.success(vibrationService.getLatestRaw(dto));
    }

    @PostMapping("/history")
    public Result<List<VibrationRawVO>> getHistoryRaw(@RequestBody VibrationQueryDTO dto) {
        return Result.success(vibrationService.getHistoryRaw(dto));
    }
}