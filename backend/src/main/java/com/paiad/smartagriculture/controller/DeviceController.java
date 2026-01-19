package com.paiad.smartagriculture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiad.smartagriculture.model.pojo.Device;
import com.paiad.smartagriculture.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
@Slf4j
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/page")
    public Page<Device> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return deviceService.page(new Page<>(page, size));
    }

    @PostMapping
    public boolean save(@RequestBody Device device) {
        return deviceService.save(device);
    }

    @PutMapping
    public boolean update(@RequestBody Device device) {
        return deviceService.updateById(device);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return deviceService.removeById(id);
    }
}
