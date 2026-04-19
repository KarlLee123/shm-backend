package com.ansteel.acceleration.controller;

import com.ansteel.acceleration.dto.AccelerationQueryDTO;
import com.ansteel.acceleration.service.AccelerationService;
import com.ansteel.acceleration.vo.AccelerationRawVO;
import com.ansteel.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/acceleration")
public class AccelerationDataController {

    private final AccelerationService accelerationService;

    public AccelerationDataController(AccelerationService accelerationService) {
        this.accelerationService = accelerationService;
    }

    @PostMapping("/latest")
    public Result<AccelerationRawVO> getLatestRaw(@RequestBody AccelerationQueryDTO dto) {
        return Result.success(accelerationService.getLatestRaw(dto));
    }

    @PostMapping("/history")
    public Result<List<AccelerationRawVO>> getHistoryRaw(@RequestBody AccelerationQueryDTO dto) {
        return Result.success(accelerationService.getHistoryRaw(dto));
    }
}