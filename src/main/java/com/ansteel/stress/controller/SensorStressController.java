package com.ansteel.stress.controller;

import com.ansteel.common.Result;
import com.ansteel.stress.dto.StressRawUploadDTO;
import com.ansteel.stress.dto.StressResultUploadDTO;
import com.ansteel.stress.service.StressService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor/stress")
public class SensorStressController {

    private final StressService stressService;

    public SensorStressController(StressService stressService) {
        this.stressService = stressService;
    }

    /**
     * 原始数据上传
     */
    @PostMapping("/raw/upload")
    public Result<Void> uploadRawData(@RequestBody StressRawUploadDTO dto) {
        stressService.uploadRawData(dto);
        return Result.success();
    }

    /**
     * Python 结果回写
     */
    @PostMapping("/result/upload")
    public Result<Void> uploadResultData(@RequestBody StressResultUploadDTO dto) {
        stressService.uploadResultData(dto);
        return Result.success();
    }
}