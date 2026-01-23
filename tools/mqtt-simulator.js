/**
 * MQTT 设备模拟器 - 用于开发测试
 * 模拟多个设备定期上报环境数据
 */

const mqtt = require('mqtt')

// MQTT 配置（根据你的 application.yml 修改）
const MQTT_CONFIG = {
  broker: 'tcp://localhost:1883',
  username: 'admin',
  password: 'public',
  clientId: 'simulator-' + Math.random().toString(16).substr(2, 8)
}

// 模拟设备列表
const DEVICES = [
  { deviceId: 'DEV-SENSOR-001', type: 'SENSOR_7IN1' },
  { deviceId: 'DEV-SENSOR-002', type: 'SENSOR_7IN1' },
  { deviceId: 'DEV-FAN-001', type: 'FAN' },
  { deviceId: 'DEV-PUMP-001', type: 'PUMP' },
  { deviceId: 'DEV-PUMP-002', type: 'PUMP' }
]

// 生成随机环境数据
function generateSensorData(deviceId) {
  return {
    deviceId: deviceId,
    temperature: (20 + Math.random() * 10).toFixed(1),
    humidity: (50 + Math.random() * 30).toFixed(1),
    illuminance: Math.floor(500 + Math.random() * 1000),
    co2: Math.floor(400 + Math.random() * 200),
    soilMoisture: (30 + Math.random() * 40).toFixed(1),
    soilPh: (6 + Math.random() * 2).toFixed(1),
    ts: new Date().toISOString()
  }
}

// 连接 MQTT Broker
const client = mqtt.connect(MQTT_CONFIG.broker, {
  clientId: MQTT_CONFIG.clientId,
  username: MQTT_CONFIG.username,
  password: MQTT_CONFIG.password,
  clean: true,
  reconnectPeriod: 5000
})

client.on('connect', () => {
  console.log('✅ 已连接到 MQTT Broker')
  console.log(`📡 开始模拟 ${DEVICES.length} 个设备...`)
  
  // 订阅控制指令主题
  DEVICES.forEach(device => {
    const cmdTopic = `smart-agri/cmd/${device.deviceId}`
    client.subscribe(cmdTopic, (err) => {
      if (!err) {
        console.log(`📥 订阅控制指令: ${cmdTopic}`)
      }
    })
  })
  
  // 每 5 秒发送一次数据/心跳
  setInterval(() => {
    DEVICES.forEach(device => {
      if (device.type.includes('SENSOR')) {
        // 传感器设备：发送完整环境数据
        const data = generateSensorData(device.deviceId)
        const topic = `smart-agri/data/${device.deviceId}`
        
        client.publish(topic, JSON.stringify(data), { qos: 1 }, (err) => {
          if (!err) {
            console.log(`📤 [${device.deviceId}] 温度: ${data.temperature}°C, 湿度: ${data.humidity}%`)
          }
        })
      } else {
        // 非传感器设备（FAN/PUMP）：发送心跳消息保持在线
        const heartbeat = {
          deviceId: device.deviceId,
          status: 'online',
          ts: new Date().toISOString()
        }
        const topic = `smart-agri/data/${device.deviceId}`
        
        client.publish(topic, JSON.stringify(heartbeat), { qos: 1 }, (err) => {
          if (!err) {
            console.log(`💓 [${device.deviceId}] 心跳`)
          }
        })
      }
    })
  }, 5000)
})

// 接收控制指令并回复 ACK
client.on('message', (topic, message) => {
  try {
    const cmd = JSON.parse(message.toString())
    console.log(`📨 收到控制指令: ${topic}`, cmd)
    
    // 提取 deviceId
    const deviceId = topic.split('/').pop()
    
    // 模拟指令执行（80% 成功率）
    setTimeout(() => {
      const success = Math.random() > 0.2
      const ackTopic = `smart-agri/ack/${deviceId}`
      const ack = {
        requestId: cmd.requestId,
        success: success,
        errorMsg: success ? null : '设备执行失败（模拟）'
      }
      
      client.publish(ackTopic, JSON.stringify(ack), { qos: 1 })
      console.log(`✅ [${deviceId}] ACK 已发送: ${success ? '成功' : '失败'}`)
    }, 1000 + Math.random() * 2000) // 1-3秒延迟
    
  } catch (e) {
    console.error('❌ 解析指令失败:', e.message)
  }
})

client.on('error', (err) => {
  console.error('❌ MQTT 错误:', err.message)
})

client.on('close', () => {
  console.log('🔌 MQTT 连接已断开')
})

console.log('🚀 MQTT 设备模拟器启动中...')
console.log(`📍 Broker: ${MQTT_CONFIG.broker}`)
