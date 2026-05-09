package com.ansteel.vibratingwire.controller;

import com.ansteel.common.Result;
import com.ansteel.vibratingwire.dto.VibratingWireQueryDTO;
import com.ansteel.vibratingwire.service.VibratingWireService;
import com.ansteel.vibratingwire.vo.VibratingWireRawVO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/data/vibrating-wire")
public class VibratingWireDataController {
    private final VibratingWireService service;
    public VibratingWireDataController(VibratingWireService service) { this.service = service; }
    @PostMapping("/latest")
    public Result<VibratingWireRawVO> latest(@RequestBody VibratingWireQueryDTO dto) { return Result.success(service.getLatestRaw(dto)); }
    @PostMapping("/history")
    public Result<List<VibratingWireRawVO>> history(@RequestBody VibratingWireQueryDTO dto) { return Result.success(service.getHistoryRaw(dto)); }
}
