package com.ansteel.vibration.controller;

import com.ansteel.common.Result;
import com.ansteel.vibration.dto.VibrationRawUploadDTO;
import com.ansteel.vibration.service.VibrationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor/vibration")
public class SensorVibrationController {

    private final VibrationService vibrationService;

    public SensorVibrationController(VibrationService vibrationService) {
        this.vibrationService = vibrationService;
    }

    @PostMapping("/upload")
    public Result<Void> uploadRawData(@RequestBody VibrationRawUploadDTO dto) {
        vibrationService.uploadRawData(dto);
        return Result.success();
    }
}