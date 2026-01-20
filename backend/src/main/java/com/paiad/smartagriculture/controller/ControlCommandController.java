package com.paiad.smartagriculture.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiad.smartagriculture.common.constants.MqttConstants;
import com.paiad.smartagriculture.model.pojo.ControlCommand;
import com.paiad.smartagriculture.service.ControlCommandService;
import com.paiad.smartagriculture.service.MqttService;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.paiad.smartagriculture.common.result.ApiResult;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/control-command")
@Slf4j
@Tag(name = "设备控制接口")
public class ControlCommandController {

    @Autowired
    private ControlCommandService controlCommandService;

    @Autowired
    private MqttService mqttService;

    @GetMapping("/page")
    @Operation(summary = "分页查询指令记录")
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
    @Operation(summary = "下发控制指令")
    public ApiResult<String> send(@RequestBody ControlCommand command) {
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

        // 尝试解析 params 为 JSON 对象，避免双重序列化导致的转义字符
        JSONObject payloadJson = JSONUtil.parseObj(commandToSend);
        try {
            if (commandToSend.getParams() != null) {
                payloadJson.set("params", JSONUtil.parseObj(commandToSend.getParams()));
            }
        } catch (Exception e) {
            // 解析失败（可能不是json格式），保持原样
            log.warn("Failed to parse params as JSON object: {}", commandToSend.getParams());
        }

        String payload = payloadJson.toString();
        mqttService.publish(topic, payload);

        // 3. Save record
        controlCommandService.save(commandToSend);
        return ApiResult.success(commandToSend.getRequestId());
    }
}
