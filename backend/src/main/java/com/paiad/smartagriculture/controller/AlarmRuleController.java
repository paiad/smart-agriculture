package com.paiad.smartagriculture.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiad.smartagriculture.model.pojo.AlarmRule;
import com.paiad.smartagriculture.service.AlarmRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm-rule")
@Slf4j
@Tag(name = "告警规则接口")
public class AlarmRuleController {

    @Autowired
    private AlarmRuleService alarmRuleService;

    @GetMapping("/page")
    @Operation(summary = "分页查询规则")
    public Page<AlarmRule> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return alarmRuleService.page(new Page<>(page, size));
    }

    @PostMapping
    @Operation(summary = "新增规则")
    public boolean save(@RequestBody AlarmRule rule) {
        return alarmRuleService.save(rule);
    }

    @PutMapping
    @Operation(summary = "更新规则")
    public boolean update(@RequestBody AlarmRule rule) {
        return alarmRuleService.updateById(rule);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除规则")
    public boolean delete(@PathVariable Long id) {
        return alarmRuleService.removeById(id);
    }
}
