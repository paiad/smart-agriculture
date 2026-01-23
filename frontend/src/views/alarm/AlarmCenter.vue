<template>
  <div class="bg-white rounded-xl border border-slate-100 shadow-sm min-h-[calc(100vh-140px)] flex flex-col">
    <div class="p-5 border-b border-slate-100 flex items-center justify-between shrink-0">
      <h2 class="text-lg font-semibold text-slate-800">告警中心</h2>
      <div class="flex gap-2">
        <el-radio-group v-model="filterStatus" size="small">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="active">未处理</el-radio-button>
          <el-radio-button label="history">历史记录</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="alarms.length === 0 && !loading" class="flex-1 flex flex-col items-center justify-center py-20">
      <div class="w-20 h-20 rounded-full bg-green-50 flex items-center justify-center mb-4">
        <div class="i-solar-check-circle-bold text-4xl text-green-400"></div>
      </div>
      <p class="text-slate-500 text-lg font-medium mb-1">暂无告警</p>
      <p class="text-slate-400 text-sm">所有设备运行正常</p>
    </div>

    <!-- Alarm List (Grouped by Device) -->
    <div v-else class="flex-1 overflow-auto p-4 space-y-4 bg-slate-50/50" v-loading="loading">
      <div 
        v-for="group in alarmGroups" 
        :key="group.deviceId"
        class="bg-white rounded-xl border border-slate-100 shadow-sm overflow-hidden"
      >
        <!-- Group Header -->
        <div 
          class="px-4 py-3 bg-white border-b border-slate-100 flex items-center justify-between cursor-pointer hover:bg-slate-50 transition-colors select-none"
          @click="toggleGroup(group.deviceId)"
        >
            <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-orange-50 flex items-center justify-center text-orange-500">
                    <div class="i-solar-bell-bing-bold-duotone text-lg"></div>
                </div>
                <div>
                    <div class="flex items-center gap-2">
                        <span class="font-bold text-slate-800 text-sm">{{ group.deviceName }}</span>
                        <span class="px-1.5 py-0.5 bg-slate-100 text-slate-500 rounded text-[11px] font-mono font-medium">
                            {{ group.deviceId }}
                        </span>
                    </div>
                    <div class="flex gap-1 mt-1.5">
                       <span class="px-2 py-0.5 rounded-full bg-red-50 text-red-600 text-[10px] font-bold">
                          {{ group.alarms.length }} 异常指标
                       </span>
                       <span v-if="group.totalCount > group.alarms.length" class="px-2 py-0.5 rounded-full bg-slate-50 text-slate-400 text-[10px] font-medium">
                          {{ group.totalCount - group.alarms.length }} 历史记录
                       </span>
                    </div>
                </div>
            </div>
            <div class="flex items-center gap-3">
                <div class="text-xs text-slate-400 font-mono hidden sm:block">
                    Latest: {{ formatTime(group.latestTime) }}
                </div>
                <!-- Arrow Icon -->
                <div class="transition-transform duration-300 text-slate-400" 
                     :class="expandedGroups[group.deviceId] ? 'rotate-180' : ''">
                    <div class="i-solar-alt-arrow-down-linear text-xl"></div>
                </div>
            </div>
        </div>

        <!-- Group Items (Collapsible) -->
        <div v-show="expandedGroups[group.deviceId]" class="transition-all duration-300 ease-in-out">
            <div 
                v-for="alarm in group.alarms" 
                :key="alarm.id"
                class="p-4 border-b border-slate-50 last:border-0 hover:bg-slate-50/50 transition-colors flex items-center gap-4 group/item"
            >
                <!-- Level Indicator -->
                <div :class="['w-1.5 h-1.5 rounded-full shrink-0', getAlarmBgColor(alarm.level)]"></div>

                <!-- Alarm Details -->
                <div class="flex-1 min-w-0 grid grid-cols-1 md:grid-cols-12 gap-4 items-center">
                    
                    <div class="md:col-span-4">
                         <div class="flex items-baseline gap-2">
                            <span class="text-sm font-semibold text-slate-700">{{ getMetricLabel(alarm.metric) }}</span>
                            <span class="text-xs text-slate-400 font-normal">({{ alarm.metric }})</span>
                         </div>
                         <div class="text-xs text-slate-500 mt-0.5">{{ formatTime(alarm.triggeredAt) }}</div>
                    </div>

                    <div class="md:col-span-5">
                        <div class="flex items-center gap-2 text-sm">
                            <span class="text-slate-500">当前值:</span>
                            <span class="font-mono font-medium text-slate-700">{{ alarm.value }}</span>
                            
                            <span class="mx-2 text-slate-300">|</span>
                            
                            <span class="text-slate-500">阈值:</span>
                            <span class="font-mono text-slate-600 text-xs bg-slate-100 px-1.5 py-0.5 rounded">
                                {{ formatThreshold(alarm) }}
                            </span>
                        </div>
                        <div class="text-xs text-red-500 mt-1" v-if="alarm.message">
                             {{ alarm.message }}
                        </div>
                    </div>

                     <!-- Actions -->
                    <div class="md:col-span-3 flex justify-end">
                        <el-button 
                            v-if="alarm.status === 0" 
                            size="small" 
                            type="primary" 
                            plain 
                            @click="handleConfirm(alarm)"
                            :loading="confirmingId === alarm.id"
                        >
                            确认处理
                        </el-button>
                        <div v-else class="flex items-center gap-1 text-green-600">
                            <div class="i-solar-check-circle-bold"></div>
                            <span class="text-xs font-medium">已解决</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="p-4 border-t border-slate-100 flex justify-between items-center shrink-0 bg-white z-10">
      <div class="text-sm text-slate-500">
        共 {{ total }} 条告警消息
      </div>
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { alarmApi, type Alarm } from '@/api/alarm'
import { deviceApi, type Device } from '@/api/device'
import { ElMessage } from 'element-plus'

const filterStatus = ref('all')
const alarms = ref<Alarm[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const confirmingId = ref<number | null>(null)
const expandedGroups = ref<Record<string, boolean>>({})
const deviceMap = ref<Record<string, string>>({})

// Toggle group expansion
const toggleGroup = (deviceId: string) => {
  expandedGroups.value[deviceId] = !expandedGroups.value[deviceId]
}

// Group alarms by device
const alarmGroups = computed(() => {
  const groups: Record<string, Alarm[]> = {}
  alarms.value.forEach(alarm => {
    if (!groups[alarm.deviceId]) {
      groups[alarm.deviceId] = []
    }
    // Safe access
    groups[alarm.deviceId]?.push(alarm)
  })
  
  return Object.entries(groups).map(([deviceId, list]) => {
     // Sort list by time desc first
     list.sort((a, b) => (b.triggeredAt || '').localeCompare(a.triggeredAt || ''))
     
     // Find latest time for sorting the groups
     const latestTime = list[0]?.triggeredAt || ''
     
     // Deduplicate metrics logic
     const uniqueAlarms: Alarm[] = []
     const seenMetrics = new Set<string>()
     
     for (const alarm of list) {
       if (!seenMetrics.has(alarm.metric)) {
         uniqueAlarms.push(alarm)
         seenMetrics.add(alarm.metric)
       }
     }

     return {
        deviceId,
        deviceName: deviceMap.value[deviceId] || deviceId, // Name fallback to ID
        alarms: uniqueAlarms,
        totalCount: list.length,
        latestTime
     }
  }).sort((a, b) => b.latestTime.localeCompare(a.latestTime))
})

const fetchDevices = async () => {
    try {
        const res = await deviceApi.getPage(1, 1000)
        const map: Record<string, string> = {}
        if (res.records) {
            res.records.forEach((d: Device) => {
                map[d.deviceId] = d.name || d.deviceId
            })
        }
        deviceMap.value = map
    } catch (e) {
        // silent error
    }
}

const fetchAlarms = async () => {
  // ... existing logic
  loading.value = true
  try {
    let status = undefined
    if (filterStatus.value === 'active') status = 0
    if (filterStatus.value === 'history') status = 1
    
    const res = await alarmApi.getPage(currentPage.value, pageSize.value, status)
    alarms.value = res.records
    total.value = res.total
  } catch (e) {
    console.error(e)
    ElMessage.error('获取告警列表失败')
  } finally {
    loading.value = false
  }
}

// ... handlers ...

onMounted(() => {
    fetchDevices()
    fetchAlarms()
})



const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchAlarms()
}

const handleConfirm = async (alarm: Alarm) => {
  confirmingId.value = alarm.id
  try {
    await alarmApi.updateStatus(alarm.id, 1)
    ElMessage.success('告警已确认处理')
    alarm.status = 1
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    confirmingId.value = null
  }
}

const getMetricLabel = (metric?: string) => {
  const map: Record<string, string> = {
    temperature: '温度',
    humidity: '湿度',
    illuminance: '光照',
    co2: 'CO2浓度',
    soilMoisture: '土壤湿度',
    soilPh: '土壤pH'
  }
  return map[metric || ''] || metric
}

const getAlarmBgColor = (level?: string) => {
  switch(level) {
    case 'critical': return 'bg-red-500'
    case 'high': return 'bg-orange-500'
    case 'medium': return 'bg-yellow-500'
    default: return 'bg-blue-500'
  }
}

const formatTime = (time?: string) => {
  if (!time) return '-'
  return time.substring(0, 19).replace('T', ' ')
}

const formatThreshold = (alarm: Alarm) => {
  const parts = []
  if (alarm.minValue !== null && alarm.minValue !== undefined) {
    parts.push(`Min: ${alarm.minValue}`)
  }
  if (alarm.maxValue !== null && alarm.maxValue !== undefined) {
    parts.push(`Max: ${alarm.maxValue}`)
  }
  return parts.join(', ') || '-'
}

watch(filterStatus, () => {
  currentPage.value = 1
  fetchAlarms()
})

onMounted(() => fetchAlarms())
</script>
