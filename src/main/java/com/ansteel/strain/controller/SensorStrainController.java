package com.ansteel.strain.controller;

import com.ansteel.common.Result;
import com.ansteel.strain.dto.StrainRawUploadDTO;
import com.ansteel.strain.service.StrainService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor/strain")
public class SensorStrainController {

    private final StrainService strainService;

    public SensorStrainController(StrainService strainService) {
        this.strainService = strainService;
    }

    @PostMapping("/upload")
    public Result<Void> uploadRawData(@RequestBody StrainRawUploadDTO dto) {
        strainService.uploadRawData(dto);
        return Result.success();
    }
}