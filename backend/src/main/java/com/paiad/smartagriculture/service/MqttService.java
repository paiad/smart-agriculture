package com.paiad.smartagriculture.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    @Value("${mqtt.pub-topic}")
    private String pubTopic;

    @Value("${mqtt.sub-topic}")
    private String subTopic;

    @PostConstruct
    public void init() {
        mqttClient.setCallback(this);
        subscribe(subTopic);
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
            // 从 topic 解析 deviceId: smart-agriculture/data/{deviceId}
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

            envDataService.processAndSave(envData);
        } catch (Exception e) {
            log.error("Failed to process MQTT message payload", e);
        }
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
        }
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
    }
}
