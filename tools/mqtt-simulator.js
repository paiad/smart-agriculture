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
  { deviceId: 'DEV-SENSOR-003', type: 'SENSOR_7IN1' },
  { deviceId: 'DEV-WEATHER-001', type: 'WEATHER_STATION' },
  { deviceId: 'DEV-WEATHER-002', type: 'WEATHER_STATION' },
  { deviceId: 'DEV-FAN-001', type: 'FAN' },
  { deviceId: 'DEV-FAN-002', type: 'FAN' },
  { deviceId: 'DEV-FAN-003', type: 'FAN' },
  { deviceId: 'DEV-PUMP-001', type: 'PUMP' },
  { deviceId: 'DEV-PUMP-002', type: 'PUMP' },
  { deviceId: 'DEV-SENSOR-TEST-01', type: 'SENSOR_7IN1' },
  { deviceId: 'DEV-FAN-TEST-01', type: 'FAN' },
  { deviceId: 'DEV-PUMP-TEST-01', type: 'PUMP' }
]

// 获取本地(上海)时间字符串
function getTimestamp() {
  const d = new Date()
  const pad = (n) => n.toString().padStart(2, '0')
  const ms = d.getMilliseconds().toString().padStart(3, '0')
  // 返回 YYYY-MM-DDTHH:mm:ss.SSS 格式
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}.${ms}`
}

// 生成随机环境数据 (改进型：基于上一次的值产生微小波动，使其平滑)
function generateSensorData(deviceId, state, isAbnormal = false) {
  // 1. 初始化或获取持久化的数值
  if (!state.lastValues) {
    state.lastValues = {
      temperature: 22.0 + Math.random() * 5,
      humidity: 50.0 + Math.random() * 20,
      illuminance: 500 + Math.random() * 500,
      co2: 400 + Math.random() * 100,
      soilHumidity: 35.0 + Math.random() * 15,
      soilPh: 6.5 + Math.random() * 0.5
    }
  }

  const last = state.lastValues

  // 2. 产生真实波动 (Random Walk)
  last.temperature += (Math.random() - 0.5) * 0.3 // 步进范围 [-0.15, 0.15]
  last.humidity += (Math.random() - 0.5) * 0.6
  last.illuminance += (Math.random() - 0.5) * 80
  last.co2 += (Math.random() - 0.5) * 15
  last.soilHumidity += (Math.random() - 0.5) * 0.4
  last.soilPh += (Math.random() - 0.5) * 0.05

  // 3. 数值边界收敛 (防止无限漂移)
  const clamp = (val, min, max) => Math.max(min, Math.min(max, val))
  last.temperature = clamp(last.temperature, 15, 38)
  last.humidity = clamp(last.humidity, 30, 95)
  last.illuminance = clamp(last.illuminance, 0, 10000)
  last.co2 = clamp(last.co2, 350, 1500)
  last.soilHumidity = clamp(last.soilHumidity, 10, 85)
  last.soilPh = clamp(last.soilPh, 5.0, 9.0)

  // 4. 构建当前数据包
  const data = {
    deviceId: deviceId,
    temperature: parseFloat(last.temperature.toFixed(1)),
    humidity: parseFloat(last.humidity.toFixed(1)),
    illuminance: Math.floor(last.illuminance),
    co2: Math.floor(last.co2),
    soilHumidity: parseFloat(last.soilHumidity.toFixed(1)),
    soilPh: parseFloat(last.soilPh.toFixed(1)),
    ts: getTimestamp()
  }

  // 5. 注入瞬时异常 (不改变持久化的 lastValues，除非你想模拟持续故障)
  if (isAbnormal) {
    const metrics = ['temperature', 'humidity', 'co2', 'soilHumidity', 'soilPh']
    const target = metrics[Math.floor(Math.random() * metrics.length)]
    
    let anomalyValue
    switch(target) {
      case 'temperature': anomalyValue = parseFloat((Math.random() > 0.5 ? 45.0 + Math.random() * 10 : -10 - Math.random() * 10).toFixed(1)); break;
      case 'humidity': anomalyValue = parseFloat((Math.random() > 0.5 ? 98.0 + Math.random() * 2 : 2 + Math.random() * 3).toFixed(1)); break;
      case 'co2': anomalyValue = Math.floor(2500 + Math.random() * 1000); break;
      case 'soilHumidity': anomalyValue = parseFloat((Math.random() > 0.5 ? 95.0 + Math.random() * 5 : 2 + Math.random() * 5).toFixed(1)); break;
      case 'soilPh': anomalyValue = parseFloat((Math.random() > 0.5 ? 12.0 + Math.random() * 2 : 1 + Math.random() * 2).toFixed(1)); break;
    }
    data[target] = anomalyValue
    console.log(`\n\x1b[33m[ANOMALY]\x1b[0m 设备 ${deviceId} 模拟异常: ${target} = ${anomalyValue}`)
  }

  return data
}

// 连接 MQTT Broker
const client = mqtt.connect(MQTT_CONFIG.broker, {
  clientId: MQTT_CONFIG.clientId,
  username: MQTT_CONFIG.username,
  password: MQTT_CONFIG.password,
  clean: true,
  reconnectPeriod: 5000,
  // 遗嘱消息：如果模拟器由于异常或断开连接（非优雅关闭），Broker 会代报当前 Client ID 相关的离线状态
  // 注意：LWT 通常只能绑定一个 Topic，对于多设备模拟器，最好使用优雅关闭逻辑手动上报所有设备离线
  will: {
    topic: `smart-agri/status/${MQTT_CONFIG.clientId}/offline`,
    payload: JSON.stringify({ status: 'offline', msg: 'Simulator disconnected unexpectedly' }),
    qos: 1,
    retain: false
  }
})

// 设备状态管理 (模拟内存存储)
const DEVICE_STATES = {}
DEVICES.forEach(d => {
  // 所有设备初始状态为 Standby (关闭)，让用户测试 "ON" 指令
  DEVICE_STATES[d.deviceId] = {
    running: false, 
    params: {}
  }
})

client.on('connect', () => {
  console.log('✅ 已连接到 MQTT Broker')
  console.log(`📡 开始模拟 ${DEVICES.length} 个设备 (默认关闭状态)...`)
  
  // 订阅控制指令主题
  DEVICES.forEach(device => {
    const cmdTopic = `smart-agri/cmd/${device.deviceId}`
    client.subscribe(cmdTopic)
  })
  
  // 定时器辅助变量，用于降低心跳频率
  let loopCount = 0

  // 定时上报数据 (每3秒)
  setInterval(() => {
    loopCount++
    DEVICES.forEach(device => {
      const state = DEVICE_STATES[device.deviceId]
      const topic = `smart-agri/data/${device.deviceId}`
      
      // 1. 构建基础心跳/状态数据
      let payload = {
        deviceId: device.deviceId,
        status: state.running ? 1 : 0, // 1:在线/运行, 0:离线/停止
        online: true, 
        ts: getTimestamp()
      }
      
      // 2. 传感器设备处理
      if (device.type.includes('SENSOR') || device.type.includes('WEATHER')) {
        if (state.running) {
          // 运行中：上报环境数据 (每3秒)
          const isAbnormal = Math.random() < 0.001
          const envData = generateSensorData(device.deviceId, state, isAbnormal)
          Object.assign(payload, envData)
          process.stdout.write(`\r📤 [${device.deviceId}] 上报数据: T:${payload.temperature}℃ H:${payload.humidity}%${isAbnormal ? ' (!)' : ''}               `)
          client.publish(topic, JSON.stringify(payload), { qos: 1 })
        } else {
          // 待机中：仅每 30 秒发送一次心跳 (10次循环)，保持在线状态，不污染数据库
          if (loopCount % 10 === 0) {
            process.stdout.write(`\r💓 [${device.deviceId}] 维持心跳 (Standby)               `)
            client.publish(topic, JSON.stringify(payload), { qos: 1 })
          }
        }
      } 
      // 3. 非传感器设备 (风机/水泵)
      else {
          // 始终上报状态 (心跳)，因为它们不产生海量流水数据
          if (loopCount % 10 === 0 || state.running) {
            process.stdout.write(`\r💓 [${device.deviceId}] 状态: ${state.running ? 'RUNNING' : 'STOPPED'}               `)
            client.publish(topic, JSON.stringify(payload), { qos: 1 })
          }
      }
    })
  }, 3000)
})

// 接收控制指令并回复 ACK
// 接收控制指令
// 接收控制指令
client.on('message', (topic, message) => {
  try {
    const cmd = JSON.parse(message.toString())
    const deviceId = topic.split('/').pop()
    const state = DEVICE_STATES[deviceId]
    
    if (!state) {
        console.warn(`\n⚠️ 收到未知设备的指令: ${deviceId}`)
        return
    }

    console.log(`\n📨 [${deviceId}] 收到指令: ${cmd.command}`, cmd.params || '')
    
    // 更新模拟状态
    if (cmd.command === 'ON') {
      state.running = true
      console.log(`   └─ 执行: 启动设备 [${deviceId}]`)
    }
    else if (cmd.command === 'OFF') {
      state.running = false
      console.log(`   └─ 执行: 停止设备 [${deviceId}]`)
    }
    else if (cmd.command === 'RESTART') {
      state.running = false
      console.log(`   └─ 执行: 重启中... [${deviceId}]`)
      setTimeout(() => { state.running = true; console.log(`   └─ [${deviceId}] 重启完成`) }, 2000)
    }

    // 回复 ACK
    setTimeout(() => {
      const ackTopic = `smart-agri/ack/${deviceId}`
      const ack = {
        requestId: cmd.requestId,
        success: true,
        message: 'Operation successful'
      }
      client.publish(ackTopic, JSON.stringify(ack), { qos: 1 })
      console.log(`   └─ ✅ ACK 已回复`)
    }, 1000)
    
  } catch (e) {
    console.error('❌ Error:', e.message)
  }
})

client.on('error', (err) => {
  console.error('❌ MQTT 错误:', err.message)
})

client.on('close', () => {
  console.log('🔌 MQTT 连接已断开')
})

// --- 优雅退出处理 ---
async function gracefulShutdown() {
  console.log('\n🛑 正在停止模拟器，上报所有设备离线状态...')
  
  const promises = DEVICES.map(device => {
    const topic = `smart-agri/data/${device.deviceId}`
    const offlinePayload = {
      deviceId: device.deviceId,
      status: 0,
      online: false, // 明确标记为离线
      ts: getTimestamp()
    }
    return new Promise((resolve) => {
      client.publish(topic, JSON.stringify(offlinePayload), { qos: 1, retain: true }, () => {
        resolve()
      })
    })
  })

  try {
    await Promise.all(promises)
    console.log('✅ 所有设备已标记为离线')
  } catch (err) {
    console.error('❌ 上报离线状态失败:', err)
  }

  client.end(true, () => {
    console.log('👋 模拟器已关闭')
    process.exit(0)
  })
}

// 监听系统信号
process.on('SIGINT', gracefulShutdown)
process.on('SIGTERM', gracefulShutdown)

console.log('🚀 MQTT 设备模拟器启动中...')
console.log(`📍 Broker: ${MQTT_CONFIG.broker}`)
