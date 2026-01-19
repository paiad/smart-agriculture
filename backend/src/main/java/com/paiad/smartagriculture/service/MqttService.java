package com.paiad.smartagriculture.service;

import cn.hutool.json.JSONUtil;
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
            // Parse JSON using Hutool
            EnvData envData = JSONUtil.toBean(payload, EnvData.class);

            if (envData != null) {
                // If deviceId is present, process it
                if (envData.getDeviceId() != null) {
                    // Set raw payload
                    envData.setRawPayload(payload);
                    // Ensure timestamp
                    if (envData.getTs() == null) {
                        envData.setTs(LocalDateTime.now());
                    }
                    envDataService.processAndSave(envData);
                } else {
                    log.warn("Received MQTT message without deviceId: {}", payload);
                }
            }
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
