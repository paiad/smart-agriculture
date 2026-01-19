```
<template>
  <div class="bg-[#F5F5F7] h-full flex flex-col font-sans overflow-hidden">
    <div class="max-w-[1600px] mx-auto w-full px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      <!-- Top KPI Section -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5 shrink-0">
        <div v-for="(stat, index) in stats" :key="index"
             class="bg-white rounded-2xl p-6 shadow-[0_2px_12px_rgba(0,0,0,0.02)] border border-white/60 hover:shadow-[0_4px_16px_rgba(0,0,0,0.04)] transition-all duration-300 relative overflow-hidden group">
          <div class="flex flex-col h-full justify-between relative z-10">
            <div class="flex items-center justify-between mb-4">
              <span class="text-[13px] font-medium text-slate-500 tracking-wide">{{ stat.label }}</span>
              <div :class="`w-9 h-9 rounded-full flex items-center justify-center bg-opacity-10 transition-transform group-hover:scale-110 ${stat.iconBg}`">
                <div :class="stat.icon" class="text-lg"></div>
              </div>
            </div>
            <div>
              <div class="flex items-baseline gap-1.5">
                <span class="text-[32px] font-semibold text-[#1D1D1F] tracking-tight">{{ stat.value }}</span>
                <span class="text-sm font-medium text-slate-400 translate-y-[-2px]">{{ stat.unit }}</span>
              </div>
              <div class="mt-3 flex items-center gap-2">
                <span :class="`w-2 h-2 rounded-full ${stat.statusColor}`"></span>
                <span class="text-xs font-medium text-slate-500">{{ stat.statusText }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Main Layout Grid -->
      <div class="flex-1 min-h-0 grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        <!-- Left: Device Status Panel -->
        <div class="lg:col-span-3 flex flex-col bg-white rounded-2xl shadow-[0_2px_12px_rgba(0,0,0,0.02)] border border-white/60 overflow-hidden h-full">
          <div class="p-5 border-b border-slate-50 shrink-0">
            <div class="flex items-center justify-between mb-1">
              <h3 class="font-semibold text-[#1D1D1F] text-[15px]">监控设备</h3>
              <div class="px-2.5 py-1 bg-slate-100 rounded-full text-[11px] font-medium text-slate-500">
                {{ devices.length }} Devices
              </div>
            </div>
          </div>
          
          <div class="flex-1 overflow-y-auto p-3 space-y-2.5 bg-[#FAFAFA]">
            <div 
              v-for="device in devices" 
              :key="device.id"
              @click="selectDevice(device)"
              :class="[
                'p-4 rounded-xl border transition-all duration-200 cursor-pointer relative group',
                selectedDevice?.deviceId === device.deviceId 
                  ? 'bg-white shadow-[0_2px_8px_rgba(0,0,0,0.04)] ring-1 ring-black/5 z-10' 
                  : 'bg-white border-transparent hover:bg-white hover:shadow-sm'
              ]"
            >
              <div class="flex justify-between items-center mb-3">
                <div class="flex items-center gap-2.5">
                  <div :class="['w-2 h-2 rounded-full shadow-sm', device.status === 1 ? 'bg-[#34C759]' : 'bg-slate-300']"></div>
                  <div class="flex flex-col">
                     <span class="font-semibold text-[#1D1D1F] text-[13px] leading-tight truncate max-w-[110px]" :title="device.deviceName">
                        {{ device.deviceName }}
                     </span>
                     <span class="text-[10px] text-slate-400 font-mono mt-0.5">{{ device.deviceId }}</span>
                  </div>
                </div>
              </div>
              
              <!-- Mini Metrics -->
              <div class="grid grid-cols-2 gap-2">
                 <div class="bg-slate-50 rounded-lg px-2.5 py-2">
                   <div class="text-[10px] text-slate-400 mb-0.5">温度</div>
                   <div class="text-[13px] font-medium text-slate-700 font-mono tracking-tight">
                     {{ deviceDataMap[device.deviceId]?.temperature ?? '--' }}°
                   </div>
                 </div>
                 <div class="bg-slate-50 rounded-lg px-2.5 py-2">
                   <div class="text-[10px] text-slate-400 mb-0.5">湿度</div>
                   <div class="text-[13px] font-medium text-slate-700 font-mono tracking-tight">
                     {{ deviceDataMap[device.deviceId]?.humidity ?? '--' }}%
                   </div>
                 </div>
              </div>
              
               <!-- Selection indicator -->
               <div v-if="selectedDevice?.deviceId === device.deviceId" 
                    class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-[#007AFF] rounded-r-lg"></div>
            </div>
          </div>
        </div>

        <!-- Right: Main Chart Area -->
        <div class="lg:col-span-9 flex flex-col bg-white rounded-2xl shadow-[0_2px_12px_rgba(0,0,0,0.02)] border border-white/60 p-6 overflow-hidden h-full">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-4 shrink-0">
            <div>
              <div class="flex items-center gap-3">
                <h2 class="text-lg font-semibold text-[#1D1D1F]">环境趋势分析</h2>
                <div v-if="selectedDevice" class="px-2.5 py-0.5 bg-[#F2F2F7] text-[#86868B] text-[11px] rounded-md font-medium">
                  {{ selectedDevice.deviceName }}
                </div>
              </div>
            </div>
            
            <!-- Apple-style Segmented Control -->
            <div class="flex items-center p-1 bg-[#767680]/10 rounded-lg h-9">
              <button 
                v-for="m in metrics" 
                :key="m.key"
                @click="currentMetric = m.key"
                :class="[
                  'px-4 h-[28px] text-[13px] font-medium rounded-[6px] transition-all duration-200 flex items-center justify-center border-none outline-none focus:outline-none',
                  currentMetric === m.key 
                    ? 'bg-white text-black shadow-[0_1px_4px_rgba(0,0,0,0.12)]' 
                    : 'text-[#636366] hover:text-black bg-transparent'
                ]"
              >
                {{ m.label }}
              </button>
            </div>
          </div>

          <div class="flex-1 w-full relative min-h-0">
             <!-- Wrapper for chart to ensure strict layout -->
             <div class="absolute inset-0 w-full h-full">
                <div ref="chartRef" class="w-full h-full"></div>
             </div>

             <!-- Empty State overlay -->
             <div v-if="!selectedDevice" class="absolute inset-0 flex flex-col items-center justify-center bg-white/90 backdrop-blur-sm z-20">
               <div class="w-20 h-20 rounded-full bg-slate-50 flex items-center justify-center mb-4 shadow-inner">
                 <i class="i-solar-chart-square-linear text-3xl text-slate-300"></i>
               </div>
               <p class="text-slate-500 font-medium">请选择一个设备查看趋势</p>
             </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed, nextTick } from 'vue'
import { deviceApi, type Device } from '@/api/device'
import { envDataApi, type EnvData } from '@/api/envData'
import * as echarts from 'echarts'

// --- State ---
const devices = ref<Device[]>([])
const deviceDataMap = ref<Record<string, EnvData>>({})
const selectedDevice = ref<Device | null>(null)
const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const metrics = [
  { key: 'temperature', label: '温度', unit: '°C', color: '#FF9500' }, 
  { key: 'humidity', label: '湿度', unit: '%', color: '#007AFF' },
  { key: 'illuminance', label: '光照', unit: 'Lux', color: '#FFCC00' },
] as const

const currentMetric = ref<typeof metrics[number]['key']>('temperature')

// --- Stats Logic ---
const stats = computed(() => {
  let tSum = 0, hSum = 0, lSum = 0, co2Sum = 0;
  let count = 0;
  
  Object.values(deviceDataMap.value).forEach(d => {
    if(d) {
      tSum += Number(d.temperature) || 0
      hSum += Number(d.humidity) || 0
      lSum += Number(d.illuminance) || 0
      co2Sum += Number(d.co2) || 0
      count++
    }
  })

  // Defaults if no data
  const tAvg = count ? (tSum / count).toFixed(1) : '0.0'
  const hAvg = count ? (hSum / count).toFixed(1) : '0.0'
  const lAvg = count ? (lSum / count).toFixed(0) : '0'
  const co2Avg = count ? (co2Sum / count).toFixed(0) : '0'

  return [
    { 
      label: '平均温度', value: tAvg, unit: '°C', 
      icon: 'i-solar-thermometer-bold', iconBg: 'bg-orange-500 text-orange-600', 
      statusColor: 'bg-[#34C759]', statusText: 'Normal'
    },
    { 
      label: '平均湿度', value: hAvg, unit: '%', 
      icon: 'i-solar-waterdrops-bold', iconBg: 'bg-blue-500 text-blue-600', 
      statusColor: 'bg-[#34C759]', statusText: 'Normal'
    },
    { 
      label: '平均光照', value: lAvg, unit: 'Lux', 
      icon: 'i-solar-sun-bold', iconBg: 'bg-yellow-500 text-yellow-600', 
      statusColor: 'bg-[#34C759]', statusText: 'Normal'
    },
    { 
      label: 'CO2 浓度', value: co2Avg, unit: 'ppm', 
      icon: 'i-solar-cloud-bold', iconBg: 'bg-teal-500 text-teal-600', 
      statusColor: 'bg-[#34C759]', statusText: 'Normal'
    },
  ]
})


// --- Initial Data Fetching ---
const fetchData = async () => {
    try {
        // Fetch devices (reduced page size for performance if needed, but 100 ok for now)
        const res = await deviceApi.getPage(1, 100)
        devices.value = res.records || []
        
        // Fetch data for sensors
        for (const d of devices.value) {
           if (d.deviceType?.includes('SENSOR')) {
               const data = await envDataApi.getLatest(d.deviceId)
               if(data) {
                   deviceDataMap.value[d.deviceId] = data
               }
           }
        }

        // Auto-select first available sensor
        if (!selectedDevice.value && devices.value.length > 0) {
            const firstSensor = devices.value.find(d => d.deviceType?.includes('SENSOR'))
            if (firstSensor) {
                selectDevice(firstSensor)
            }
        }
        
        // Force chart update after data valid
        nextTick(() => {
           updateChart()
        })
    } catch (e) {
        console.error("Failed to fetch data", e)
    }
}

// --- Device Selection & Chart ---
const selectDevice = (device: Device) => {
    selectedDevice.value = device
    nextTick(() => {
        updateChart()
    })
}


// --- Chart Logic ---
const initChart = () => {
    if (!chartRef.value) return
    
    // Dispose if exists
    if (chartInstance) {
        chartInstance.dispose()
    }

    chartInstance = echarts.init(chartRef.value)
    
    // Resize observer
    window.addEventListener('resize', handleResize)
    
    // Initial empty option to prevent blank if data delayed
    chartInstance.setOption({
        title: {
            text: 'Loading...',
            left: 'center',
            textStyle: { color: '#ccc', fontSize: 14 }
        }
    })
}

const handleResize = () => {
    chartInstance?.resize()
}

// Mock data generator for smooth curves (Replace with API history later)
const generateMockHistory = () => {
    const now = new Date()
    const data = []
    
    // Base value based on metric
    let baseValue = 0
    let variance = 0
    if (currentMetric.value === 'temperature') { baseValue = 24; variance = 3; }
    if (currentMetric.value === 'humidity') { baseValue = 55; variance = 10; }
    if (currentMetric.value === 'illuminance') { baseValue = 800; variance = 200; }

    let value = baseValue
    
    for (let i = 0; i < 60; i++) {
        const time = new Date(now.getTime() - (59 - i) * 60 * 1000) // Last 60 minutes
        const randomChange = (Math.random() - 0.5) * (variance / 5)
        value += randomChange
        
        // Clamp logic
        if(currentMetric.value === 'humidity') value = Math.max(0, Math.min(100, value))
        
        data.push({
            name: time.toString(),
            value: [
                [time.getHours(), time.getMinutes()].map(pad).join(':'),
                value.toFixed(1)
            ]
        })
    }
    return data
}

const pad = (n: number) => n < 10 ? `0${n}` : n

const updateChart = () => {
    if (!chartInstance) {
        initChart() // Try init if null
    }
    if (!chartInstance || !selectedDevice.value) return
    
    // IMPORTANT: Check container size
    const width = chartRef.value?.clientWidth
    const height = chartRef.value?.clientHeight
    if (!width || !height) {
        // Retry a bit later if container not ready
        setTimeout(updateChart, 100)
        return
    }
    
    // Force resize to ensure it fits container
    chartInstance.resize()

    const metricConfig = metrics.find(m => m.key === currentMetric.value)!
    const data = generateMockHistory()
    const xData = data.map(item => item.value[0])
    const yData = data.map(item => item.value[1])

    const option = {
        grid: {
            top: 30,
            right: 20,
            bottom: 40,
            left: 50,
            containLabel: true
        },
        tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderColor: 'rgba(0,0,0,0.05)',
            textStyle: { color: '#1D1D1F', fontSize: 13 },
            padding: [10, 15],
            extraCssText: 'box-shadow: 0 4px 12px rgba(0,0,0,0.08); border-radius: 8px;'
        },
        xAxis: {
            type: 'category',
            data: xData,
            boundaryGap: false,
            axisLine: { 
                show: true,
                lineStyle: { color: '#E2E8F0' } 
            },
            axisTick: { show: false },
            axisLabel: { 
                show: true,
                color: '#64748B', 
                fontSize: 11, 
                margin: 16 
            }
        },
        yAxis: {
            type: 'value',
            splitLine: { lineStyle: { type: 'dashed', color: '#E5E5EA' } },
            axisLabel: { color: '#86868B', fontSize: 11 }
        },
        series: [
            {
                name: metricConfig.label,
                type: 'line',
                smooth: true,
                showSymbol: false,
                symbolSize: 8,
                itemStyle: {
                    color: metricConfig.color,
                    borderWidth: 2,
                    borderColor: '#fff'
                },
                lineStyle: {
                    width: 3,
                    color: metricConfig.color
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: metricConfig.color + '33' }, // 20% opacity
                        { offset: 1, color: metricConfig.color + '00' }  // 0% opacity
                    ])
                },
                emphasis: {
                     focus: 'series'
                },
                data: yData
            }
        ]
    }
    
    chartInstance.setOption(option, true) // merge: false (true here to not merge but replace) -> second arg is notMerge
}

// Watchers
watch([currentMetric, selectedDevice], () => {
    updateChart()
})

// Lifecycle
onMounted(() => {
    initChart()
    fetchData() // this triggers updateChart via nextTick
    
    // Double check chart size after a small delay to handle layout transitions
    setTimeout(() => {
        chartInstance?.resize()
    }, 500)
    
    // Poll updates - DISABLED for stability
    // setInterval(() => {
    //    updateChart() 
    // }, 5000)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    chartInstance?.dispose()
})
</script>

<style scoped>
/* Custom scrollbar for device list */
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}
.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}
.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #E5E5EA;
  border-radius: 4px;
}
.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #D1D1D6;
}
</style>
