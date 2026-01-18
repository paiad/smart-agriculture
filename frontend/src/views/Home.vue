<template>
  <div class="space-y-6">
    <!-- Welcome Section -->
    <div class="card-base p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-slate-800 mb-1">欢迎回来，管理员</h1>
          <p class="text-slate-500">{{ greeting }}，祝您工作愉快！</p>
        </div>
        <div class="text-right">
          <div class="text-sm text-slate-400">{{ currentDate }}</div>
          <div class="text-2xl font-semibold text-slate-800 tabular-nums">{{ currentTime }}</div>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-4 gap-5">
      <div 
        v-for="stat in stats" 
        :key="stat.title"
        class="card-base card-hover p-5 cursor-pointer"
      >
        <div class="flex items-center justify-between mb-4">
          <div 
            class="w-11 h-11 rounded-lg flex items-center justify-center"
            :style="{ background: stat.bgColor }"
          >
            <el-icon :size="22" :style="{ color: stat.color }">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <span 
            class="text-xs font-medium px-2 py-1 rounded-full"
            :class="stat.trend.startsWith('+') ? 'bg-green-50 text-green-600' : 'bg-amber-50 text-amber-600'"
          >
            {{ stat.trend }}
          </span>
        </div>
        <div class="text-2xl font-bold text-slate-800 mb-1">{{ stat.value }}</div>
        <div class="text-sm text-slate-500">{{ stat.title }}</div>
      </div>
    </div>

    <!-- Content Grid -->
    <div class="grid grid-cols-3 gap-5">
      <!-- Quick Actions -->
      <div class="card-base p-5">
        <h3 class="text-base font-semibold text-slate-800 mb-4">快捷操作</h3>
        <div class="grid grid-cols-2 gap-3">
          <div 
            v-for="action in quickActions" 
            :key="action.title"
            class="flex flex-col items-center p-4 rounded-lg bg-slate-50 hover:bg-brand-50 cursor-pointer transition-colors group"
          >
            <el-icon :size="22" class="text-slate-400 group-hover:text-brand-500 mb-2 transition-colors">
              <component :is="action.icon" />
            </el-icon>
            <span class="text-sm text-slate-600 group-hover:text-slate-800 transition-colors">{{ action.title }}</span>
          </div>
        </div>
      </div>

      <!-- Recent Activity -->
      <div class="card-base p-5 col-span-2">
        <h3 class="text-base font-semibold text-slate-800 mb-4">最近活动</h3>
        <div class="space-y-3">
          <div 
            v-for="(activity, idx) in recentActivities" 
            :key="idx"
            class="flex items-center gap-4 p-3 rounded-lg hover:bg-slate-50 transition-colors cursor-pointer"
          >
            <div 
              class="w-9 h-9 rounded-lg flex items-center justify-center"
              :style="{ background: activity.bgColor }"
            >
              <el-icon :size="16" :style="{ color: activity.color }">
                <component :is="activity.icon" />
              </el-icon>
            </div>
            <div class="flex-1 min-w-0">
              <div class="text-sm font-medium text-slate-700 truncate">{{ activity.title }}</div>
              <div class="text-xs text-slate-400">{{ activity.time }}</div>
            </div>
            <span 
              class="text-xs font-medium px-2 py-1 rounded-full whitespace-nowrap"
              :class="activity.statusClass"
            >
              {{ activity.status }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Monitor, Connection, Warning, DataLine, Plus, Upload, Document, Search } from '@element-plus/icons-vue'

const stats = [
  { 
    title: '设备总数', 
    value: '128', 
    icon: Monitor, 
    color: '#3B82F6', 
    bgColor: '#EFF6FF',
    trend: '+12%'
  },
  { 
    title: '在线设备', 
    value: '96', 
    icon: Connection, 
    color: '#10B981', 
    bgColor: '#ECFDF5',
    trend: '+8%'
  },
  { 
    title: '告警数量', 
    value: '5', 
    icon: Warning, 
    color: '#F59E0B', 
    bgColor: '#FFFBEB',
    trend: '-15%'
  },
  { 
    title: '今日采集', 
    value: '1.2K', 
    icon: DataLine, 
    color: '#8B5CF6', 
    bgColor: '#F5F3FF',
    trend: '+24%'
  }
]

const quickActions = [
  { title: '添加设备', icon: Plus },
  { title: '数据上传', icon: Upload },
  { title: '查看报告', icon: Document },
  { title: '高级搜索', icon: Search },
]

const recentActivities = [
  { 
    title: '温室 A 区温度传感器已上线', 
    time: '5 分钟前', 
    icon: Connection,
    color: '#10B981',
    bgColor: '#ECFDF5',
    status: '已完成',
    statusClass: 'bg-green-50 text-green-600'
  },
  { 
    title: '土壤湿度数据采集完成', 
    time: '15 分钟前', 
    icon: DataLine,
    color: '#3B82F6',
    bgColor: '#EFF6FF',
    status: '已完成',
    statusClass: 'bg-blue-50 text-blue-600'
  },
  { 
    title: '灌溉系统需要维护', 
    time: '1 小时前', 
    icon: Warning,
    color: '#F59E0B',
    bgColor: '#FFFBEB',
    status: '待处理',
    statusClass: 'bg-amber-50 text-amber-600'
  },
]

const currentTime = ref('')
const currentDate = ref('')
let timer: number

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '上午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  currentDate.value = now.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric', weekday: 'long' })
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>
