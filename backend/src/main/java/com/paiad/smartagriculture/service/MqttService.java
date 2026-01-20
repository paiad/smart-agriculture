package com.paiad.smartagriculture.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paiad.smartagriculture.common.constants.MqttConstants;
import com.paiad.smartagriculture.model.pojo.ControlCommand;
import com.paiad.smartagriculture.model.pojo.Device;
import com.paiad.smartagriculture.model.pojo.EnvData;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class MqttService implements MqttCallback {

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private EnvDataService envDataService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ControlCommandService controlCommandService;

    @Value("${mqtt.pub-topic}")
    private String pubTopic;

    @Value("${mqtt.sub-topic}")
    private String subTopic;

    @Value("${mqtt.ack-topic}")
    private String ackTopic;

    @PostConstruct
    public void init() {
        mqttClient.setCallback(this);
        subscribe(subTopic);
        subscribe(ackTopic);
        log.info("Subscribed to data and ack topics");
    }

    public void publish(String content) {
        publish(pubTopic, content);
    }

    public void publish(String topic, String content) {
        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(1);
            mqttClient.publish(topic, message);
            log.info("Message published to topic {}: {}", topic, content);
        } catch (MqttException e) {
            log.error("Failed to publish message: {}", e.getMessage());
        }
    }

    public void subscribe(String topic) {
        try {
            mqttClient.subscribe(topic, 1);
            log.info("Subscribed to topic: {}", topic);
        } catch (MqttException e) {
            log.error("Failed to subscribe to topic {}: {}", topic, e.getMessage());
        }
    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        log.warn("MQTT disconnected: {}", disconnectResponse.getReasonString());
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        log.error("MQTT error occurred: {}", exception.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        log.info("Message arrived on topic {}: {}", topic, payload);

        try {
            if (topic.startsWith(MqttConstants.TOPIC_DATA)) {
                handleEnvData(topic, payload);
            } else if (topic.startsWith(MqttConstants.TOPIC_ACK)) {
                handleAck(topic, payload);
            } else {
                log.warn("Unknown topic: {}", topic);
            }
        } catch (Exception e) {
            log.error("Failed to process MQTT message payload", e);
        }
    }

    private void handleEnvData(String topic, String payload) {
        // 从 topic 解析 deviceId: smart-agri/data/{deviceId}
        String[] topicParts = topic.split("/");
        if (topicParts.length < 3) {
            log.warn("Invalid topic format: {}", topic);
            return;
        }
        String topicDeviceId = topicParts[topicParts.length - 1];

        // 1. 先解析 JSON（无 IO）
        EnvData envData = JSONUtil.toBean(payload, EnvData.class);
        if (envData == null) {
            log.warn("Failed to parse message payload: {}", payload);
            return;
        }

        // 2. 校验 JSON 中的 deviceId 与 topic 是否一致（无 IO）
        if (envData.getDeviceId() != null && !envData.getDeviceId().equals(topicDeviceId)) {
            log.warn("deviceId 不一致，拒绝存储: topic={}, payload={}", topicDeviceId, envData.getDeviceId());
            return;
        }

        // 3. 校验设备是否已注册（有 IO）
        Device device = deviceService.getOne(new LambdaQueryWrapper<Device>()
                .eq(Device::getDeviceId, topicDeviceId));
        if (device == null) {
            log.warn("未注册设备上报数据，忽略: {}", topicDeviceId);
            return;
        }

        // 使用 topic 中的 deviceId
        envData.setDeviceId(topicDeviceId);

        // 设置原始载荷和时间戳
        envData.setRawPayload(payload);
        if (envData.getTs() == null) {
            envData.setTs(LocalDateTime.now());
        }

        // 更新设备心跳
        deviceService.updateHeartbeat(topicDeviceId);

        envDataService.processAndSave(envData);
    }

    private void handleAck(String topic, String payload) {
        // 从 topic 解析 deviceId: smart-agri/ack/{deviceId}
        String[] topicParts = topic.split("/");
        if (topicParts.length < 3) {
            log.warn("Invalid ACK topic format: {}", topic);
            return;
        }
        String deviceId = topicParts[topicParts.length - 1];

        // 更新设备心跳
        deviceService.updateHeartbeat(deviceId);

        // 解析 ACK 负载: { "requestId": "xxx", "success": true/false, "errorMsg": "..." }
        JSONObject ackJson = JSONUtil.parseObj(payload);
        String requestId = ackJson.getStr("requestId");
        Boolean success = ackJson.getBool("success");
        String errorMsg = ackJson.getStr("errorMsg");

        if (requestId == null) {
            log.warn("ACK 消息缺少 requestId: {}", payload);
            return;
        }

        // 根据 requestId 查找指令记录
        ControlCommand cmd = controlCommandService.getOne(new LambdaQueryWrapper<ControlCommand>()
                .eq(ControlCommand::getRequestId, requestId));
        if (cmd == null) {
            log.warn("未找到对应的指令记录: requestId={}", requestId);
            return;
        }

        // 更新指令状态
        cmd.setStatus(Boolean.TRUE.equals(success) ? 2 : 3); // 2=成功, 3=失败
        cmd.setAckAt(LocalDateTime.now());
        if (errorMsg != null) {
            cmd.setErrorMsg(errorMsg);
        }
        controlCommandService.updateById(cmd);
        log.info("ACK 处理完成: requestId={}, success={}", requestId, success);
    }

    @Override
    public void deliveryComplete(IMqttToken token) {
        log.debug("Message delivery complete");
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("MQTT connection complete. Reconnect: {}", reconnect);
        if (reconnect) {
            subscribe(subTopic);
            subscribe(ackTopic);
        }
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
    }
}
