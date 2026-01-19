<template>
  <div class="space-y-6">
    <!-- Header / Summary -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div v-for="(stat, index) in stats" :key="index" class="bg-white rounded-xl p-5 border border-slate-100 shadow-sm flex items-center justify-between">
        <div>
          <div class="text-sm text-slate-500 mb-1">{{ stat.label }}</div>
          <div class="text-2xl font-bold text-slate-800">{{ stat.value }} <span class="text-xs text-slate-400 font-normal">{{ stat.unit }}</span></div>
        </div>
        <div :class="`w-10 h-10 rounded-full flex items-center justify-center ${stat.bgClass}`">
          <div :class="stat.icon" class="text-white text-xl"></div>
        </div>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      
      <!-- Device/Zone List -->
      <div class="bg-white rounded-xl border border-slate-100 shadow-sm overflow-hidden flex flex-col h-[600px]">
        <div class="p-4 border-b border-slate-100 flex items-center justify-between">
          <h3 class="font-semibold text-slate-800">在线设备</h3>
          <span class="text-xs bg-green-50 text-green-600 px-2 py-0.5 rounded-full font-medium">{{ onlineCount }} Online</span>
        </div>
        <div class="flex-1 overflow-y-auto p-2 space-y-2">
            <div 
              v-for="device in devices" 
              :key="device.id"
              class="p-3 rounded-lg border border-transparent hover:bg-slate-50 hover:border-slate-100 cursor-pointer transition-all group"
            >
              <div class="flex items-center justify-between mb-2">
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 rounded-full" :class="device.status === 1 ? 'bg-green-500' : 'bg-slate-300'"></div>
                  <span class="font-medium text-slate-700">{{ device.deviceName }}</span>
                </div>
                <span class="text-xs text-slate-400">{{ device.lastActiveTime?.substring(11, 19) }}</span>
              </div>
              <div v-if="deviceDataMap[device.deviceId]" class="flex items-center gap-4 text-xs text-slate-500">
                <span>温度: {{ deviceDataMap[device.deviceId]?.temperature }}°C</span>
                <span>湿度: {{ deviceDataMap[device.deviceId]?.humidity }}%</span>
              </div>
              <div v-else class="text-xs text-slate-400 italic">暂无数据</div>
            </div>
        </div>
      </div>

      <!-- Real-time Chart -->
      <div class="lg:col-span-2 bg-white rounded-xl border border-slate-100 shadow-sm p-5 h-[600px] flex flex-col">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="font-semibold text-slate-800 text-lg">环境趋势监控</h3>
            <p class="text-sm text-slate-400 mt-0.5">实时更新频率: 5s</p>
          </div>
          <div class="flex bg-slate-100 rounded-lg p-1">
            <button class="px-3 py-1 text-xs font-medium rounded-md bg-white shadow-sm text-slate-700">温度</button>
            <button class="px-3 py-1 text-xs font-medium rounded-md text-slate-500 hover:text-slate-700">湿度</button>
            <button class="px-3 py-1 text-xs font-medium rounded-md text-slate-500 hover:text-slate-700">光照</button>
          </div>
        </div>
        
        <!-- Placeholder for ECharts/Chart -->
        <div class="flex-1 bg-slate-50 rounded-lg border border-dashed border-slate-200 flex items-center justify-center text-slate-400">
            [Chart Component Placeholder]
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { deviceApi, type Device } from '@/api/device'
import { envDataApi, type EnvData } from '@/api/envData'

const devices = ref<Device[]>([])
const onlineCount = computed(() => devices.value.filter(d => d.status === 1).length)

// Stats
const stats = ref([
  { label: '平均温度', value: '0.0', unit: '°C', icon: 'i-solar-thermometer-linear', bgClass: 'bg-orange-400' },
  { label: '平均湿度', value: '0.0', unit: '%', icon: 'i-solar-waterdrops-linear', bgClass: 'bg-blue-400' },
  { label: '当前光照', value: '0', unit: 'Lux', icon: 'i-solar-sun-linear', bgClass: 'bg-yellow-400' },
  { label: 'CO2 浓度', value: '0', unit: 'ppm', icon: 'i-solar-cloud-linear', bgClass: 'bg-green-400' },
])

const deviceDataMap = ref<Record<string, EnvData>>({})

const fetchDevices = async () => {
    try {
        const res = await deviceApi.getPage(1, 100)
        devices.value = res.records || []
        // Fetch latest data for each device
        devices.value.forEach(d => {
            // Fix: inclusive check for different sensor types like SENSOR_7IN1
            if (d.deviceType?.includes('SENSOR')) {
                fetchDeviceData(d.deviceId)
            }
        })
    } catch (e: any) {
        console.error(e)
        // ElMessage handling is done in request.ts, but we catch it here to prevent crash
    }
}

const fetchDeviceData = async (deviceId: string) => {
    try {
        const res = await envDataApi.getLatest(deviceId)
        if (res) {
            deviceDataMap.value[deviceId] = res
            // Update stats logic here (simplified for now, just taking the first one or average)
            updateStats()
        }
    } catch (e) {
        console.error(e)
    }
}

const updateStats = () => {
    let tempSum = 0, humiditySum = 0, luxSum = 0, co2Sum = 0;
    let count = 0;
    Object.values(deviceDataMap.value).forEach(data => {
        if (data) {
            tempSum += data.temperature || 0
            humiditySum += data.humidity || 0
            luxSum += data.illuminance || 0
            co2Sum += data.co2 || 0
            count++
        }
    })
    
    if (count > 0) {
        if (stats.value[0]) stats.value[0].value = (tempSum / count).toFixed(1)
        if (stats.value[1]) stats.value[1].value = (humiditySum / count).toFixed(1)
        if (stats.value[2]) stats.value[2].value = (luxSum / count).toFixed(0)
        if (stats.value[3]) stats.value[3].value = (co2Sum / count).toFixed(0)
    }
}

onMounted(() => {
    fetchDevices()
    // Poll every 5 seconds
    // Poll every 5 seconds (Disabled per user request)
    // setInterval(() => {
    //     devices.value.forEach(d => {
    //         if (d.deviceType?.includes('SENSOR') && d.status === 1) {
    //             fetchDeviceData(d.deviceId)
    //         }
    //     })
    // }, 5000)
})
</script>
