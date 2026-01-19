package com.paiad.smartagriculture;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.paiad.smartagriculture.model.pojo.Device;
import com.paiad.smartagriculture.service.DeviceService;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MQTT 数据模拟器 (测试模式)
 * 启动后会向 smart-agriculture/sub 主题发送模拟传感数据
 */
@SpringBootTest
public class MqttDataSimulatorTest {

    @Autowired
    private DeviceService deviceService;

    // MQTT Broker 地址
    private static final String BROKER = "tcp://localhost:1883";
    // 上报主题
    private static final String TOPIC = "smart-agriculture/sub";

    @Test
    public void startSimulation() {
        try {
            MqttClient client = connectMqtt();
            System.out.println("Starting data simulation... Stop the test to exit.");

            while (true) {
                // 实时获取数据库中的设备列表
                List<Device> devices = deviceService.list();
                List<String> deviceIds = devices.stream()
                        .map(Device::getDeviceId)
                        .filter(id -> id != null && !id.isEmpty())
                        .collect(Collectors.toList());

                if (deviceIds.isEmpty()) {
                    System.out.println("No devices found in database. Waiting...");
                }

                for (String deviceId : deviceIds) {
                    publishData(client, deviceId);
                }

                // 间隔 5 秒
                ThreadUtil.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MqttClient connectMqtt() throws Exception {
        String clientId = "Simulator_" + System.currentTimeMillis();
        MqttClient client = new MqttClient(BROKER, clientId, new MemoryPersistence());

        MqttConnectionOptions connOpts = new MqttConnectionOptions();
        connOpts.setCleanStart(true);
        connOpts.setKeepAliveInterval(60);
        connOpts.setAutomaticReconnect(true);

        System.out.println("Connecting to broker: " + BROKER);
        client.connect(connOpts);
        System.out.println("Connected to MQTT Broker");
        return client;
    }

    private void publishData(MqttClient client, String deviceId) throws Exception {
        // 构造模拟数据
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("ts", LocalDateTime.now().toString());

        // 模拟各项环境指标 (使用 NumberUtil 保留小数位)
        // TMP: 5~40 -> 生成 -10~50 (以便触发高低温报警)
        data.put("temperature", NumberUtil.round(RandomUtil.randomDouble(-10.0, 50.0), 1));
        // HR: 20~95 -> 生成 10~99 (以便触发湿度报警)
        data.put("humidity", NumberUtil.round(RandomUtil.randomDouble(10.0, 99.0), 1));
        // ILL: 0~120000 -> 生成 0~130000
        data.put("illuminance", RandomUtil.randomInt(0, 130000));
        // CO2: 300~2000 -> 生成 200~3000
        data.put("co2", RandomUtil.randomInt(200, 3000));
        // sTMP: 0~35 -> 生成 -5~45
        data.put("soilTemp", NumberUtil.round(RandomUtil.randomDouble(-5.0, 45.0), 1));
        // sHR: 10~90 -> 生成 0~100
        data.put("soilHumidity", NumberUtil.round(RandomUtil.randomDouble(0.0, 100.0), 1));
        // sPH: 4.5~8.5 -> 生成 3.0~10.0
        data.put("soilPh", NumberUtil.round(RandomUtil.randomDouble(3.0, 10.0), 1));
        // sEC: 0~5000 -> 生成 0~6000
        data.put("soilEc", RandomUtil.randomInt(0, 6000));
        // N/P/K: 0~300/200/400 -> 生成更大范围
        data.put("nitrogen", NumberUtil.round(RandomUtil.randomDouble(0, 400), 1));
        data.put("phosphorus", NumberUtil.round(RandomUtil.randomDouble(0, 300), 1));
        data.put("potassium", NumberUtil.round(RandomUtil.randomDouble(0, 600), 1));
        // PRS: 80~110 -> 70~120
        data.put("pressure", NumberUtil.round(RandomUtil.randomDouble(70.0, 120.0), 1));
        // FS: 0~20 -> 0~30
        data.put("windSpeed", NumberUtil.round(RandomUtil.randomDouble(0.0, 30.0), 1));
        // RVC: 0~500 -> 0~600
        data.put("rainfall", NumberUtil.round(RandomUtil.randomDouble(0.0, 600.0), 1));
        data.put("precip", RandomUtil.randomInt(0, 2)); // 0 or 1
        data.put("windDir", "SE");

        String payload = JSONUtil.toJsonStr(data);

        MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        message.setQos(1);

        client.publish(TOPIC, message);
        System.out.println("Published to " + deviceId + ": " + payload);
    }
}
