<template>
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
    <div v-for="device in controlDevices" :key="device.id" class="bg-white rounded-xl border border-slate-100 shadow-sm hover:shadow-md transition-shadow">
      <!-- Card Header -->
      <div class="p-5 border-b border-slate-50 flex items-start justify-between">
        <div class="flex items-center gap-3">
          <div class="w-12 h-12 rounded-xl bg-blue-50 text-blue-500 flex items-center justify-center text-2xl">
            <div :class="device.icon"></div>
          </div>
          <div>
            <h3 class="font-semibold text-slate-800">{{ device.deviceName }}</h3>
            <p class="text-xs text-slate-400 mt-0.5">{{ device.location }} | ID: {{ device.id }}</p>
          </div>
        </div>
        <div 
          class="px-2 py-1 rounded-md text-xs font-medium"
          :class="device.isOn ? 'bg-green-50 text-green-600' : 'bg-slate-100 text-slate-500'"
        >
          {{ device.isOn ? '运行中' : '已停止' }}
        </div>
      </div>

      <!-- Control Area -->
      <div class="p-5 space-y-4">
        <!-- Switch -->
        <div class="flex items-center justify-between">
            <span class="text-sm text-slate-600 font-medium">电源开关</span>
            <el-switch v-model="device.isOn" @change="() => handleSwitchChange(device)" />
        </div>

        <!-- Slider (Speed/Intensity) -->
        <div v-if="device.hasSlider">
            <div class="flex items-center justify-between mb-2">
                <span class="text-sm text-slate-600 font-medium">运行强度</span>
                <span class="text-xs text-slate-400">{{ device.value }}%</span>
            </div>
            <el-slider v-model="device.value" :disabled="!device.isOn" size="small" />
        </div>
      </div>

      <!-- Footer Actions -->
      <div class="px-5 py-3 bg-slate-50 border-t border-slate-100 rounded-b-xl flex items-center justify-between">
         <span class="text-xs text-slate-400">上次操作: 15min ago</span>
         <el-button type="primary" link size="small">查看日志</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { deviceApi, type Device } from '@/api/device'
import { controlApi } from '@/api/control'
import { ElMessage } from 'element-plus'

interface ControlDevice extends Device {
    icon: string
    isOn: boolean
    hasSlider: boolean
    value: number
}

const controlDevices = ref<ControlDevice[]>([])

const fetchControlDevices = async () => {
    try {
        const res = await deviceApi.getPage(1, 100)
        // Filter for actuators or specific control devices
        // For demo, we treat all non-SENSOR devices or specific types as controllable
        // Or we can just list all for now to see.
        // Let's assume devices with type 'ACTUATOR' or similar are controllable.
        // Since backend data might be scant, we'll map them.
        
        controlDevices.value = res.records.map(d => ({
            ...d,
            icon: getIconForDevice(d),
            isOn: d.status === 1, // This is device status, not necessarily switch status. 
            // Real status should probably come from EnvData or a separate state field.
            // For now, let's assume status 1 is ON.
            hasSlider: d.deviceType === 'LIGHT' || d.deviceType === 'FAN', // Example logic
            value: 0
        }))
    } catch (e) {
        console.error(e)
    }
}

const getIconForDevice = (d: Device) => {
    if (d.deviceName.includes('风机') || d.deviceType === 'FAN') return 'i-solar-fan-linear'
    if (d.deviceName.includes('灯') || d.deviceType === 'LIGHT') return 'i-solar-lightbulb-minimalistic-linear'
    if (d.deviceName.includes('泵') || d.deviceType === 'PUMP') return 'i-solar-waterdrops-linear'
    return 'i-solar-server-square-linear'
}

const handleSwitchChange = async (device: ControlDevice) => {
    try {
        const command = device.isOn ? 'ON' : 'OFF'
        await controlApi.sendCommand({
            deviceId: device.deviceId,
            command: command,
            params: '{}'
        })
        ElMessage.success(`发送指令 ${command} 成功`)
    } catch (e) {
        device.isOn = !device.isOn // Revert on failure
        ElMessage.error('发送指令失败')
    }
}

onMounted(() => fetchControlDevices())
</script>
