package com.ansteel.strain.controller;

import com.ansteel.common.Result;
import com.ansteel.strain.dto.StrainQueryDTO;
import com.ansteel.strain.service.StrainService;
import com.ansteel.strain.vo.StrainRawVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/strain")
public class StrainDataController {

    private final StrainService strainService;

    public StrainDataController(StrainService strainService) {
        this.strainService = strainService;
    }

    @PostMapping("/latest")
    public Result<StrainRawVO> getLatestRaw(@RequestBody StrainQueryDTO dto) {
        return Result.success(strainService.getLatestRaw(dto));
    }

    @PostMapping("/history")
    public Result<List<StrainRawVO>> getHistoryRaw(@RequestBody StrainQueryDTO dto) {
        return Result.success(strainService.getHistoryRaw(dto));
    }
}