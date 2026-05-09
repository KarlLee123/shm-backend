package com.ansteel.fiber.controller;

import com.ansteel.common.Result;
import com.ansteel.fiber.dto.FiberQueryDTO;
import com.ansteel.fiber.service.FiberService;
import com.ansteel.fiber.vo.FiberRawVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/fiber")
public class FiberDataController {
    private final FiberService fiberService;
    public FiberDataController(FiberService fiberService) { this.fiberService = fiberService; }
    @PostMapping("/latest")
    public Result<FiberRawVO> latest(@RequestBody FiberQueryDTO dto) { return Result.success(fiberService.getLatestRaw(dto)); }
    @PostMapping("/history")
    public Result<List<FiberRawVO>> history(@RequestBody FiberQueryDTO dto) { return Result.success(fiberService.getHistoryRaw(dto)); }
}
