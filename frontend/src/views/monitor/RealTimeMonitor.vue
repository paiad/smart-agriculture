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
              <div :class="`w-9 h-9 rounded-full flex items-center justify-center transition-transform group-hover:scale-110 ${stat.iconBg}`">
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
      <div class="flex-1 min-h-0 grid grid-cols-1 lg:grid-cols-12 gap-6 h-[calc(100vh-280px)]">
        
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
          
          <div class="flex-1 overflow-y-auto p-4 space-y-3 bg-[#F8FAFC] custom-scrollbar">
            <div 
              v-for="device in devices" 
              :key="device.id"
              @click="selectDevice(device)"
              :class="[
                'p-4 rounded-xl border transition-all duration-300 cursor-pointer relative group',
                selectedDevice?.deviceId === device.deviceId 
                  ? 'bg-white shadow-[0_8px_20px_rgba(0,0,0,0.06)] ring-1 ring-brand-100 border-brand-200 z-10 scale-[1.02]' 
                  : 'bg-white border-transparent shadow-sm hover:shadow-md hover:scale-[1.01]',
                device.online !== 1 ? 'opacity-60 grayscale-[0.8] hover:grayscale-0 hover:opacity-100' : ''
              ]"
            >
              <div class="flex justify-between items-center mb-3">
                <div class="flex items-center gap-3">
                  <div class="relative">
                    <div :class="['w-2.5 h-2.5 rounded-full shadow-sm', device.online === 1 ? 'bg-[#34C759]' : 'bg-slate-300']"></div>
                    <div v-if="device.online === 1" class="absolute inset-0 rounded-full bg-[#34C759] animate-ping opacity-30"></div>
                  </div>
                  <div class="flex flex-col min-w-0">
                     <span class="font-bold text-slate-800 text-sm leading-tight truncate max-w-[120px]" :title="device.name || device.deviceId">
                        {{ device.name || device.deviceId }}
                     </span>
                     <span class="text-[10px] text-slate-400 font-mono mt-0.5">{{ device.deviceId }}</span>
                  </div>
                </div>
              </div>
              
              <!-- Mini Metrics -->
              <div v-if="device.deviceType?.includes('SENSOR')" class="grid grid-cols-2 gap-2">
                 <div class="bg-indigo-50/50 rounded-lg px-3 py-2 border border-indigo-100/50">
                   <div class="text-[10px] text-indigo-400 mb-0.5 font-medium">温度</div>
                   <div class="text-[14px] font-bold text-indigo-600 font-mono tracking-tight flex items-baseline gap-0.5">
                     {{ deviceDataMap[device.deviceId]?.temperature ?? '--' }} <span class="text-[10px] font-normal opacity-70">°C</span>
                   </div>
                 </div>
                 <div class="bg-sky-50/50 rounded-lg px-3 py-2 border border-sky-100/50">
                   <div class="text-[10px] text-sky-400 mb-0.5 font-medium">湿度</div>
                   <div class="text-[14px] font-bold text-sky-600 font-mono tracking-tight flex items-baseline gap-0.5">
                     {{ deviceDataMap[device.deviceId]?.humidity ?? '--' }} <span class="text-[10px] font-normal opacity-70">%</span>
                   </div>
                 </div>
              </div>

               <!-- Empty state for non-sensor devices -->
               <div v-else class="py-2 flex items-center justify-center bg-slate-50 rounded-lg border border-slate-100 border-dashed">
                 <span class="text-[11px] text-slate-400">执行设备 (无环境数据)</span>
               </div>
              
               <!-- Selection indicator -->
               <div v-if="selectedDevice?.deviceId === device.deviceId" 
                    class="absolute left-0 top-6 bottom-6 w-1 bg-brand-500 rounded-r-full shadow-[2px_0_8px_rgba(59,130,246,0.3)]"></div>
            </div>
          </div>
        </div>

        <!-- Right: Main Chart Area -->
        <div class="lg:col-span-9 flex flex-col bg-white rounded-2xl shadow-[0_2px_12px_rgba(0,0,0,0.02)] border border-white/60 p-6 overflow-hidden h-full">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-4 shrink-0">
            <div>
              <div class="flex items-center gap-3">
                <h2 class="text-lg font-bold text-slate-800 tracking-tight">环境趋势分析</h2>
                <div v-if="selectedDevice" class="px-3 py-1 bg-brand-50 text-brand-600 text-[12px] rounded-full font-medium border border-brand-100/50 animate-fade-in">
                  {{ selectedDevice.name || selectedDevice.deviceId }}
                </div>
              </div>
            </div>
            
            <!-- Apple-style Segmented Control -->
            <div class="flex items-center p-1 bg-slate-100/80 rounded-xl h-10">
              <button 
                v-for="m in metrics" 
                :key="m.key"
                @click="currentMetric = m.key"
                :class="[
                  'px-4 h-[32px] text-[13px] font-medium rounded-[8px] transition-all duration-300 flex items-center justify-center border-none outline-none focus:outline-none',
                  currentMetric === m.key 
                    ? 'bg-white text-brand-600 shadow-[0_2px_8px_rgba(0,0,0,0.08)] scale-100' 
                    : 'text-slate-500 hover:text-slate-700 bg-transparent hover:bg-black/5 scale-95'
                ]"
              >
                {{ m.label }}
              </button>
            </div>
          </div>

          <div class="flex-1 w-full relative min-h-0 bg-gradient-to-b from-slate-50/50 to-white rounded-xl border border-slate-100/50 p-4">
             <!-- Wrapper for chart to ensure strict layout -->
             <div class="absolute inset-4 w-[calc(100%-2rem)] h-[calc(100%-2rem)]">
                <div ref="chartRef" class="w-full h-full"></div>
             </div>

             <!-- Empty State / Offline / Non-sensor Overlay -->
             <div v-if="chartOverlayState !== 'READY'" class="absolute inset-0 flex flex-col items-center justify-center bg-white/90 backdrop-blur-sm z-50 rounded-xl transition-all duration-300">
               <div :class="['w-24 h-24 rounded-full flex items-center justify-center mb-5 shadow-inner border border-slate-100', 
                  chartOverlayState === 'OFFLINE' ? 'bg-slate-100/50' : 
                  chartOverlayState === 'NOT_SENSOR' ? 'bg-orange-50' : 'bg-slate-50']">
                 <div :class="['text-4xl', 
                    chartOverlayState === 'OFFLINE' ? 'i-solar-cloud-broken-bold text-slate-400' :
                    chartOverlayState === 'NOT_SENSOR' ? 'i-solar-forbidden-circle-linear text-orange-300' : 
                    'i-solar-chart-square-linear text-slate-300']"></div>
               </div>
               <p class="text-slate-600 font-medium text-lg">
                 {{ chartOverlayState === 'EMPTY' ? '请选择一个设备查看趋势' : 
                    chartOverlayState === 'OFFLINE' ? '设备已离线' :
                    '该设备无环境监测数据' }}
               </p>
               <p class="text-slate-400 text-sm mt-2">
                 {{ chartOverlayState === 'EMPTY' ? '点击左侧列表中的设备卡片' : 
                    chartOverlayState === 'OFFLINE' ? '离线状态下无法查看实时环境数据' :
                    '只有传感器设备支持环境趋势分析' }}
               </p>
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
const rawDevices = ref<Device[]>([])
const deviceDataMap = ref<Record<string, EnvData>>({})
const selectedDevice = ref<Device | null>(null)
const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

// Computed: Search & Sort
const devices = computed(() => {
  return [...rawDevices.value].sort((a, b) => {
    // Priority: Online > Offline
    const aOnline = a.online === 1 ? 1 : 0
    const bOnline = b.online === 1 ? 1 : 0
    if (aOnline !== bOnline) return bOnline - aOnline
    
    // Secondary: Name or ID
    const aName = a.name || a.deviceId
    const bName = b.name || b.deviceId
    return aName.localeCompare(bName)
  })
})

const metrics = [
  { key: 'temperature', label: '温度', unit: '°C', color: '#6366f1' }, 
  { key: 'humidity', label: '湿度', unit: '%', color: '#0ea5e9' },
  { key: 'illuminance', label: '光照', unit: 'Lux', color: '#f59e0b' },
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
      icon: 'i-solar-thermometer-bold', iconBg: 'bg-indigo-500 text-white shadow-indigo-200',
      statusColor: 'bg-[#34C759]', statusText: 'Normal'
    },
    { 
      label: '平均湿度', value: hAvg, unit: '%', 
      icon: 'i-solar-waterdrops-bold', iconBg: 'bg-sky-500 text-white shadow-sky-200', 
      statusColor: 'bg-[#34C759]', statusText: 'Normal'
    },
    { 
      label: '平均光照', value: lAvg, unit: 'Lux', 
      icon: 'i-solar-sun-bold', iconBg: 'bg-amber-500 text-white shadow-amber-200', 
      statusColor: 'bg-[#34C759]', statusText: 'Normal'
    },
    { 
      label: 'CO2 浓度', value: co2Avg, unit: 'ppm', 
      icon: 'i-solar-cloud-bold', iconBg: 'bg-emerald-500 text-white shadow-emerald-200', 
      statusColor: 'bg-[#34C759]', statusText: 'Normal'
    },
  ]
})

// Computed: Chart Overlay State
const chartOverlayState = computed(() => {
  if (!selectedDevice.value) return 'EMPTY'
  if (selectedDevice.value.online !== 1) return 'OFFLINE'
  if (!selectedDevice.value.deviceType?.includes('SENSOR')) return 'NOT_SENSOR'
  return 'READY'
})



// --- Initial Data Fetching ---
const fetchData = async () => {
    try {
        const res = await deviceApi.getPage(1, 100)
        rawDevices.value = res.records || []
        
        for (const d of rawDevices.value) {
           if (d.deviceType?.includes('SENSOR')) {
               const data = await envDataApi.getLatest(d.deviceId)
               if(data) {
                   deviceDataMap.value[d.deviceId] = data
               }
           }
        }

        if (!selectedDevice.value && devices.value.length > 0) {
            const firstOnlineSensor = devices.value.find(d => d.deviceType?.includes('SENSOR') && d.online === 1)
            const fallback = devices.value[0]
            if (firstOnlineSensor) {
                selectDevice(firstOnlineSensor)
            } else if (fallback) {
                selectDevice(fallback)
            }
        }
        
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
    
    if (chartInstance) {
        chartInstance.dispose()
    }

    chartInstance = echarts.init(chartRef.value)
    
    window.addEventListener('resize', handleResize)
    
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

const generateMockHistory = () => {
    const now = new Date()
    const data = []
    
    let baseValue = 0
    let variance = 0
    if (currentMetric.value === 'temperature') { baseValue = 24; variance = 3; }
    if (currentMetric.value === 'humidity') { baseValue = 55; variance = 10; }
    if (currentMetric.value === 'illuminance') { baseValue = 800; variance = 200; }

    let value = baseValue
    
    for (let i = 0; i < 60; i++) {
        const time = new Date(now.getTime() - (59 - i) * 60 * 1000)
        const randomChange = (Math.random() - 0.5) * (variance / 5)
        value += randomChange
        
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

// inside updateChart:
const updateChart = () => {
    if (!chartInstance) {
        initChart() // Try init if null
    }
    // Check if device is selected AND is a sensor
    if (!chartInstance || !selectedDevice.value) return
    
    // If NOT a sensor, clear chart or do nothing (the overlay covers it)
    if (!selectedDevice.value.deviceType?.includes('SENSOR')) {
        chartInstance.clear()
        return
    }

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
            top: 40,
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
            extraCssText: 'box-shadow: 0 4px 12px rgba(0,0,0,0.08); border-radius: 12px;'
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
            splitLine: { lineStyle: { type: 'dashed', color: '#F1F5F9' } },
            axisLabel: { color: '#94a3b8', fontSize: 11 }
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
                    color: metricConfig.color,
                },
                areaStyle: {
                    opacity: 0.05,
                    color: metricConfig.color
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
    setInterval(() => {
       fetchData() // Poll for new data
    }, 5000)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    chartInstance?.dispose()
})
</script>

<style scoped>
/* Custom scrollbar for device list */
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #CBD5E1;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94A3B8;
}

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}
</style>
