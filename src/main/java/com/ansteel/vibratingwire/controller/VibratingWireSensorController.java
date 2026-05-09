package com.ansteel.vibratingwire.controller;

import com.ansteel.common.Result;
import com.ansteel.vibratingwire.dto.VibratingWireRawUploadDTO;
import com.ansteel.vibratingwire.service.VibratingWireService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor/vibrating-wire")
public class VibratingWireSensorController {
    private final VibratingWireService service;
    public VibratingWireSensorController(VibratingWireService service) { this.service = service; }
    @PostMapping("/raw/upload")
    public Result<Void> uploadRaw(@RequestBody VibratingWireRawUploadDTO dto) { service.uploadRawData(dto); return Result.success(); }
}
