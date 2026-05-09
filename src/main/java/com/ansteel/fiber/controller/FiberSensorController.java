package com.ansteel.fiber.controller;

import com.ansteel.common.Result;
import com.ansteel.fiber.dto.FiberRawUploadDTO;
import com.ansteel.fiber.service.FiberService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor/fiber")
public class FiberSensorController {
    private final FiberService fiberService;
    public FiberSensorController(FiberService fiberService) { this.fiberService = fiberService; }
    @PostMapping("/raw/upload")
    public Result<Void> uploadRaw(@RequestBody FiberRawUploadDTO dto) {
        fiberService.uploadRawData(dto);
        return Result.success();
    }
}
