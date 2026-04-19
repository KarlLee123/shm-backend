package com.ansteel.displacement.controller;

import com.ansteel.common.Result;
import com.ansteel.displacement.dto.DisplacementRawUploadDTO;
import com.ansteel.displacement.service.DisplacementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor/displacement")
public class SensorDisplacementController {

    private final DisplacementService displacementService;

    public SensorDisplacementController(DisplacementService displacementService) {
        this.displacementService = displacementService;
    }

    @PostMapping("/upload")
    public Result<Void> uploadRawData(@RequestBody DisplacementRawUploadDTO dto) {
        displacementService.uploadRawData(dto);
        return Result.success();
    }
}