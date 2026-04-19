package com.ansteel.displacement.controller;

import com.ansteel.common.Result;
import com.ansteel.displacement.dto.DisplacementQueryDTO;
import com.ansteel.displacement.service.DisplacementService;
import com.ansteel.displacement.vo.DisplacementRawVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/displacement")
public class DisplacementDataController {

    private final DisplacementService displacementService;

    public DisplacementDataController(DisplacementService displacementService) {
        this.displacementService = displacementService;
    }

    @PostMapping("/latest")
    public Result<DisplacementRawVO> getLatestRaw(@RequestBody DisplacementQueryDTO dto) {
        return Result.success(displacementService.getLatestRaw(dto));
    }

    @PostMapping("/history")
    public Result<List<DisplacementRawVO>> getHistoryRaw(@RequestBody DisplacementQueryDTO dto) {
        return Result.success(displacementService.getHistoryRaw(dto));
    }
}