package com.paiad.smartagriculture.common.constants;

/**
 * MQTT 相关常量
 */
public final class MqttConstants {

    private MqttConstants() {
    }

    /**
     * Topic 前缀
     */
    public static final String TOPIC_PREFIX = "smart-agri/";

    /**
     * 设备数据上报 Topic 前缀
     */
    public static final String TOPIC_DATA = TOPIC_PREFIX + "data/";

    /**
     * 设备控制指令 Topic 前缀
     */
    public static final String TOPIC_CMD = TOPIC_PREFIX + "cmd/";
}
