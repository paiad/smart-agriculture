package com.paiad.smartagriculture.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiad.smartagriculture.model.pojo.AlarmRule;
import com.paiad.smartagriculture.service.AlarmRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm-rule")
@Slf4j
public class AlarmRuleController {

    @Autowired
    private AlarmRuleService alarmRuleService;

    @GetMapping("/page")
    public Page<AlarmRule> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return alarmRuleService.page(new Page<>(page, size));
    }

    @PostMapping
    public boolean save(@RequestBody AlarmRule rule) {
        return alarmRuleService.save(rule);
    }

    @PutMapping
    public boolean update(@RequestBody AlarmRule rule) {
        return alarmRuleService.updateById(rule);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return alarmRuleService.removeById(id);
    }
}
