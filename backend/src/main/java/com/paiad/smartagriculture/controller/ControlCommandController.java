package com.paiad.smartagriculture.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiad.smartagriculture.common.constants.MqttConstants;
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
        // 1. Prepare command with Builder
        ControlCommand commandToSend = ControlCommand.builder()
                .deviceId(command.getDeviceId())
                .command(command.getCommand())
                .params(command.getParams())
                .requestId(IdUtil.fastSimpleUUID())
                .status(1) // 1: Sent
                .sentAt(LocalDateTime.now())
                .build();

        // 2. Publish MQTT
        String topic = MqttConstants.TOPIC_CMD + commandToSend.getDeviceId();
        String payload = cn.hutool.json.JSONUtil.toJsonStr(commandToSend);
        mqttService.publish(topic, payload);

        // 3. Save record
        return controlCommandService.save(commandToSend);
    }
}
