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
import com.paiad.smartagriculture.model.vo.ControlCommandVO;

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
    public ApiResult<ControlCommandVO> send(@RequestBody ControlCommand command) {
        // 1. 构建控制指令对象
        ControlCommand commandToSend = ControlCommand.builder()
                .deviceId(command.getDeviceId())
                .command(command.getCommand())
                .params(command.getParams())
                .requestId(IdUtil.fastSimpleUUID())
                .status(1) // 1: 已发送
                .sentAt(LocalDateTime.now())
                .build();

        // 2. 发送 MQTT 消息
        String topic = MqttConstants.TOPIC_CMD + commandToSend.getDeviceId();

        // 尝试解析 params 为 JSON 对象，避免双重序列化导致的转义字符
        JSONObject payloadJson = JSONUtil.parseObj(commandToSend);
        Object parsedParams = commandToSend.getParams();
        try {
            if (commandToSend.getParams() != null) {
                parsedParams = JSONUtil.parseObj(commandToSend.getParams());
                payloadJson.set("params", parsedParams);
            }
        } catch (Exception e) {
            // 解析失败（可能不是json格式），保持原样
            log.warn("Failed to parse params as JSON object: {}", commandToSend.getParams());
        }

        String payload = payloadJson.toString();
        mqttService.publish(topic, payload);

        // 3. 保存指令记录
        controlCommandService.save(commandToSend);

        // 4. 构建返回 VO 对象
        ControlCommandVO vo = ControlCommandVO.builder()
                .requestId(commandToSend.getRequestId())
                .deviceId(commandToSend.getDeviceId())
                .command(commandToSend.getCommand())
                .params(parsedParams)
                .status(commandToSend.getStatus())
                .statusText(ControlCommandVO.getStatusText(commandToSend.getStatus()))
                .sentAt(commandToSend.getSentAt())
                .build();

        return ApiResult.success(vo);
    }

    /**
     * 指令超时时间（秒）
     */
    private static final int COMMAND_TIMEOUT_SECONDS = 30;

    @GetMapping("/status/{requestId}")
    @Operation(summary = "查询指令执行状态")
    public ApiResult<ControlCommandVO> getStatus(@PathVariable String requestId) {
        ControlCommand cmd = controlCommandService.getByRequestId(requestId);
        if (cmd == null) {
            return ApiResult.error("指令不存在");
        }

        // 如果状态是"已发送"且已超时，自动标记为超时
        if (cmd.getStatus() == 1 && cmd.getSentAt() != null) {
            LocalDateTime timeout = cmd.getSentAt().plusSeconds(COMMAND_TIMEOUT_SECONDS);
            if (LocalDateTime.now().isAfter(timeout)) {
                cmd.setStatus(4); // 超时
                cmd.setErrorMsg("指令执行超时，未收到设备确认");
                controlCommandService.updateById(cmd);
            }
        }

        // 解析 params
        Object parsedParams = cmd.getParams();
        try {
            if (cmd.getParams() != null) {
                parsedParams = JSONUtil.parseObj(cmd.getParams());
            }
        } catch (Exception ignored) {
        }

        ControlCommandVO vo = ControlCommandVO.builder()
                .requestId(cmd.getRequestId())
                .deviceId(cmd.getDeviceId())
                .command(cmd.getCommand())
                .params(parsedParams)
                .status(cmd.getStatus())
                .statusText(ControlCommandVO.getStatusText(cmd.getStatus()))
                .sentAt(cmd.getSentAt())
                .build();

        return ApiResult.success(vo);
    }
}
