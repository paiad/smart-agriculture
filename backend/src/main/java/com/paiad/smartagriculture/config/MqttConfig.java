package com.paiad.smartagriculture.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MQTT 配置类
 * 支持延迟初始化和后台自动重连，避免 Broker 不可用时应用无法启动
 */
@Configuration
@Slf4j
public class MqttConfig {

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    private MqttClient mqttClient;
    private final AtomicBoolean connecting = new AtomicBoolean(false);

    /**
     * 创建 MQTT 连接选项
     */
    private MqttConnectionOptions createConnectionOptions() {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setUserName(username);
        options.setPassword(password.getBytes());
        options.setCleanStart(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);
        return options;
    }

    /**
     * MQTT Client Bean
     * 使用 @Lazy 延迟初始化，首次使用时才连接
     * 连接失败时返回未连接的 client，不阻止应用启动
     */
    @Bean
    @Lazy
    public MqttClient mqttClient() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(brokerUrl, clientId, persistence);
            connect();
            return mqttClient;
        } catch (MqttException e) {
            log.error("Failed to create MQTT client: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 尝试连接 MQTT Broker
     */
    private void connect() {
        if (mqttClient == null) {
            return;
        }

        // 防止并发连接
        if (!connecting.compareAndSet(false, true)) {
            return;
        }

        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect(createConnectionOptions());
                log.info("Connected to MQTT broker: {}", brokerUrl);
            }
        } catch (MqttException e) {
            log.warn("MQTT connection failed, will retry in background: {}", e.getMessage());
        } finally {
            connecting.set(false);
        }
    }

    /**
     * 定时检查并重连 MQTT
     * 每 30 秒检查一次，如果断开则尝试重连
     */
    @Scheduled(fixedRate = 30000, initialDelay = 5000)
    public void reconnectIfNeeded() {
        if (mqttClient == null) {
            // 尝试创建 client
            try {
                MemoryPersistence persistence = new MemoryPersistence();
                mqttClient = new MqttClient(brokerUrl, clientId, persistence);
                log.info("MQTT client created in background");
            } catch (MqttException e) {
                log.warn("Failed to create MQTT client: {}", e.getMessage());
                return;
            }
        }

        if (!mqttClient.isConnected()) {
            log.info("MQTT disconnected, attempting reconnection...");
            connect();
        }
    }

    /**
     * 检查 MQTT 是否已连接
     */
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }
}
