package com.paiad.smartagriculture.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiad.smartagriculture.model.pojo.EnvData;
import com.paiad.smartagriculture.service.EnvDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/env-data")
@Slf4j
@Tag(name = "环境数据接口")
public class EnvDataController {

    @Autowired
    private EnvDataService envDataService;

    @GetMapping("/page")
    @Operation(summary = "分页查询历史数据")
    public Page<EnvData> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String deviceId) {
        LambdaQueryWrapper<EnvData> wrapper = new LambdaQueryWrapper<>();
        if (deviceId != null && !deviceId.isBlank()) {
            wrapper.eq(EnvData::getDeviceId, deviceId);
        }
        wrapper.orderByDesc(EnvData::getTs);
        return envDataService.page(new Page<>(page, size), wrapper);
    }

    @GetMapping("/latest")
    @Operation(summary = "查询最新数据")
    public EnvData getLatest(@RequestParam String deviceId) {
        return envDataService.getOne(new LambdaQueryWrapper<EnvData>()
                .eq(EnvData::getDeviceId, deviceId)
                .orderByDesc(EnvData::getTs)
                .last("LIMIT 1"));
    }
}
