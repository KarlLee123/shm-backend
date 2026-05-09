package com.ansteel.vibrationdat.controller;

import com.ansteel.common.Result;
import com.ansteel.vibrationdat.dto.VibrationDatQueryDTO;
import com.ansteel.vibrationdat.service.VibrationDatService;
import com.ansteel.vibrationdat.vo.VibrationDatVO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/data/vibration-dat")
public class VibrationDatDataController {
    private final VibrationDatService service;
    public VibrationDatDataController(VibrationDatService service) { this.service = service; }
    @PostMapping("/latest")
    public Result<VibrationDatVO> latest(@RequestBody VibrationDatQueryDTO dto) { return Result.success(service.getLatestRaw(dto)); }
    @PostMapping("/history")
    public Result<List<VibrationDatVO>> history(@RequestBody VibrationDatQueryDTO dto) { return Result.success(service.getHistoryRaw(dto)); }
}
