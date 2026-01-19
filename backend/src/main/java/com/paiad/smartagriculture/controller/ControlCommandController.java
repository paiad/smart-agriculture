package com.paiad.smartagriculture.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiad.smartagriculture.model.pojo.ControlCommand;
import com.paiad.smartagriculture.service.ControlCommandService;
import com.paiad.smartagriculture.service.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.hutool.core.util.IdUtil;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/control-command")
@Slf4j
public class ControlCommandController {

    @Autowired
    private ControlCommandService controlCommandService;

    @Autowired
    private MqttService mqttService;

    @GetMapping("/page")
    public Page<ControlCommand> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String deviceId) {
        LambdaQueryWrapper<ControlCommand> wrapper = new LambdaQueryWrapper<>();
        if (deviceId != null && !deviceId.isBlank()) {
            wrapper.eq(ControlCommand::getDeviceId, deviceId);
        }
        wrapper.orderByDesc(ControlCommand::getCreatedAt);
        return controlCommandService.page(new Page<>(page, size), wrapper);
    }

    @PostMapping("/send")
    public boolean send(@RequestBody ControlCommand command) {
        // 1. Prepare command
        command.setRequestId(IdUtil.fastSimpleUUID());
        command.setStatus(1); // 1: Sent (assuming Mqtt send is instant otherwise should be 0 then 1)
        command.setSentAt(LocalDateTime.now());

        // 2. Publish MQTT
        // Topic: smart-agriculture/cmd/{deviceId}
        String topic = "smart-agriculture/cmd/" + command.getDeviceId();
        String payload = cn.hutool.json.JSONUtil.toJsonStr(command);
        mqttService.publish(topic, payload);

        // 3. Save record
        return controlCommandService.save(command);
    }
}
