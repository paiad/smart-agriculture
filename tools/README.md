# MQTT 设备模拟器

用于开发阶段模拟设备上报数据和响应控制指令。

## 使用方法

### 1. 安装依赖

```bash
cd tools
npm install
```

### 2. 配置 MQTT Broker

修改 `mqtt-simulator.js` 中的 MQTT 配置：

```javascript
const MQTT_CONFIG = {
  broker: "tcp://localhost:1883", // MQTT Broker 地址
  username: "admin", // 用户名
  password: "public", // 密码
  clientId: "simulator-...",
};
```

### 3. 启动模拟器

```bash
npm start
```

## 功能

- ✅ 每 5 秒自动上报传感器数据（温度、湿度、光照等）
- ✅ 自动响应控制指令（80% 成功率模拟）
- ✅ 支持多设备模拟
- ✅ 自动重连

## 模拟设备列表

- `DEV-SENSOR-001` - 七合一传感器
- `DEV-SENSOR-002` - 七合一传感器
- `DEV-FAN-001` - 风扇
- `DEV-PUMP-001` - 水泵
- `DEV-PUMP-002` - 水泵

可在 `mqtt-simulator.js` 中修改 `DEVICES` 数组添加更多设备。
