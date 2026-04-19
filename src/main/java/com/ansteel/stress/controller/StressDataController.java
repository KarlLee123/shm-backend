package com.ansteel.stress.controller;

import com.ansteel.common.Result;
import com.ansteel.stress.dto.StressQueryDTO;
import com.ansteel.stress.dto.StressVerifyDTO;
import com.ansteel.stress.service.StressService;
import com.ansteel.stress.vo.StressMonitorVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/stress")
public class StressDataController {

    private final StressService stressService;

    public StressDataController(StressService stressService) {
        this.stressService = stressService;
    }

    /**
     * 人工核验
     */
    @PostMapping("/verify")
    public Result<Void> verifyStress(@RequestBody StressVerifyDTO dto) {
        stressService.verifyStress(dto);
        return Result.success();
    }

    /**
     * 查询最新一条业务可见数据
     * 默认只返回 VERIFIED
     */
    @PostMapping("/latest")
    public Result<StressMonitorVO> getLatest(@RequestBody StressQueryDTO dto) {
        return Result.success(stressService.getLatest(dto));
    }

    /**
     * 查询历史业务可见数据
     * 默认只返回 VERIFIED
     */
    @PostMapping("/history")
    public Result<List<StressMonitorVO>> getHistory(@RequestBody StressQueryDTO dto) {
        return Result.success(stressService.getHistory(dto));
    }
}