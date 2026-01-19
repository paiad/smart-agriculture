package com.paiad.smartagriculture.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiad.smartagriculture.model.pojo.Alarm;
import com.paiad.smartagriculture.service.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm")
@Slf4j
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @GetMapping("/page")
    public Page<Alarm> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Alarm> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Alarm::getStatus, status);
        }
        wrapper.orderByDesc(Alarm::getTriggeredAt);
        return alarmService.page(new Page<>(page, size), wrapper);
    }

    @PutMapping("/{id}/status")
    public boolean updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Alarm alarm = new Alarm();
        alarm.setId(id);
        alarm.setStatus(status);
        return alarmService.updateById(alarm);
    }
}
