<template>
  <div class="bg-white rounded-xl border border-slate-100 shadow-sm h-full flex flex-col">
    <div class="p-5 border-b border-slate-100 flex items-center justify-between">
      <h2 class="text-lg font-semibold text-slate-800">告警中心</h2>
      <div class="flex gap-2">
         <el-radio-group v-model="filterStatus" size="small">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="active">未处理</el-radio-button>
            <el-radio-button label="history">历史记录</el-radio-button>
         </el-radio-group>
      </div>
    </div>

    <!-- Alarm List -->
    <div class="flex-1 overflow-auto p-0">
        <div 
            v-for="(alarm, i) in alarms" 
            :key="i"
            class="p-4 border-b border-slate-50 hover:bg-slate-50 transition-colors flex items-start gap-4 last:border-0"
        >
            <!-- Icon -->
            <div class="mt-1 w-10 h-10 rounded-full flex items-center justify-center shrink-0" :class="getAlarmColor(alarm.level)">
                <div class="i-solar-bell-bing-bold text-white text-lg"></div>
            </div>

            <!-- Content -->
            <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between mb-1">
                    <h4 class="text-sm font-semibold text-slate-800">告警: {{ alarm.metric }}</h4>
                    <span class="text-xs text-slate-400">{{ alarm.triggeredAt?.substring(0, 19).replace('T', ' ') }}</span>
                </div>
                <p class="text-sm text-slate-600 mb-2">{{ alarm.message }} (Value: {{ alarm.value }})</p>
                <div class="flex items-center gap-2">
                    <span class="px-1.5 py-0.5 bg-slate-100 text-slate-500 rounded text-[10px] font-medium">{{ alarm.deviceId }}</span>
                    <span class="px-1.5 py-0.5 bg-slate-100 text-slate-500 rounded text-[10px] font-medium">{{ alarm.metric }}</span>
                </div>
            </div>

            <!-- Actions -->
            <div class="self-center">
                <el-button v-if="alarm.status === 0" size="small" type="primary" plain @click="handleConfirm(alarm)">确认处理</el-button>
                <span v-else class="text-xs text-green-600 font-medium">已解决</span>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { alarmApi, type Alarm } from '@/api/alarm'
import { ElMessage } from 'element-plus'

const filterStatus = ref('all')
const alarms = ref<Alarm[]>([])

const fetchAlarms = async () => {
    try {
        let status = undefined
        if (filterStatus.value === 'active') status = 0
        if (filterStatus.value === 'history') status = 1
        
        const res = await alarmApi.getPage(1, 100, status)
        alarms.value = res.records
    } catch (e) {
        console.error(e)
    }
}

const handleConfirm = async (alarm: Alarm) => {
    try {
        await alarmApi.updateStatus(alarm.id, 1) // 1 for Resolved
        ElMessage.success('告警已确认处理')
        // Optimistic update or refresh
        alarm.status = 1
        // fetchAlarms() 
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

const getAlarmColor = (level: string) => {
    switch(level) {
        case 'critical': return 'bg-red-500'
        case 'high': return 'bg-orange-500'
        case 'medium': return 'bg-yellow-500'
        default: return 'bg-blue-500'
    }
}

watch(filterStatus, () => fetchAlarms())

onMounted(() => fetchAlarms())
</script>
