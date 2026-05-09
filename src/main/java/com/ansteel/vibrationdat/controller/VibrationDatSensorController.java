package com.ansteel.vibrationdat.controller;

import com.ansteel.common.Result;
import com.ansteel.vibrationdat.dto.VibrationDatUploadDTO;
import com.ansteel.vibrationdat.service.VibrationDatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor/vibration-dat")
public class VibrationDatSensorController {
    private final VibrationDatService service;
    public VibrationDatSensorController(VibrationDatService service) { this.service = service; }
    @PostMapping("/raw/upload")
    public Result<Void> uploadRaw(@RequestBody VibrationDatUploadDTO dto) {
        service.uploadRawData(dto);
        return Result.success();
    }
}
