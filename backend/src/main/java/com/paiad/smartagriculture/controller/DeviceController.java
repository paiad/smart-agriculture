package com.paiad.smartagriculture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiad.smartagriculture.model.pojo.Device;
import com.paiad.smartagriculture.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
@Slf4j
@Tag(name = "设备管理接口")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/page")
    @Operation(summary = "分页查询设备列表")
    public Page<Device> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return deviceService.page(new Page<>(page, size));
    }

    @PostMapping
    @Operation(summary = "新增设备")
    public boolean save(@RequestBody Device device) {
        return deviceService.save(device);
    }

    @PutMapping
    @Operation(summary = "更新设备信息")
    public boolean update(@RequestBody Device device) {
        return deviceService.updateById(device);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除设备")
    public boolean delete(@PathVariable Long id) {
        return deviceService.removeById(id);
    }
}
