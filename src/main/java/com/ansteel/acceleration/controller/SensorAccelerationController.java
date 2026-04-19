package com.ansteel.acceleration.controller;

import com.ansteel.acceleration.dto.AccelerationRawUploadDTO;
import com.ansteel.acceleration.service.AccelerationService;
import com.ansteel.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor/acceleration")
public class SensorAccelerationController {

    private final AccelerationService accelerationService;

    public SensorAccelerationController(AccelerationService accelerationService) {
        this.accelerationService = accelerationService;
    }

    @PostMapping("/upload")
    public Result<Void> uploadRawData(@RequestBody AccelerationRawUploadDTO dto) {
        accelerationService.uploadRawData(dto);
        return Result.success();
    }
}