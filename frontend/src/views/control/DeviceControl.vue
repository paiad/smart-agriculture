<template>
  <div class="bg-[#F5F5F7] min-h-[calc(100vh-140px)] p-6">
    <div class="max-w-[1600px] mx-auto grid grid-cols-1 lg:grid-cols-12 gap-6">
      
      <!-- Left: Control Panel -->
      <div class="lg:col-span-4 space-y-5">
        <!-- Send Command Card -->
        <div class="bg-white rounded-2xl shadow-sm border border-white/60 p-6">
          <h3 class="text-lg font-semibold text-slate-800 mb-5 flex items-center gap-2">
            <div class="i-solar-gamepad-bold text-xl text-brand-500"></div>
            发送控制指令
          </h3>
          
          <el-form :model="form" label-position="top" class="space-y-4">
            <el-form-item label="目标设备" required>
              <el-select 
                v-model="form.deviceId" 
                placeholder="选择设备" 
                class="w-full"
                filterable
              >
                <el-option 
                  v-for="device in controllableDevices" 
                  :key="device.deviceId" 
                  :label="`${device.name || device.deviceId} (${device.deviceType})`"
                  :value="device.deviceId"
                >
                  <div class="flex items-center justify-between w-full">
                    <span>{{ device.name || device.deviceId }}</span>
                    <span class="text-xs text-slate-400">{{ device.deviceType }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="指令类型" required>
              <el-select v-model="form.command" placeholder="选择指令" class="w-full">
                <el-option label="开启" value="ON" />
                <el-option label="关闭" value="OFF" />
                <el-option label="设置参数" value="SET" />
                <el-option label="重启" value="RESTART" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="指令参数 (JSON)">
              <el-input 
                v-model="form.params" 
                type="textarea" 
                :rows="3"
                placeholder='例如: {"speed": 50, "duration": 60}'
              />
            </el-form-item>
            
            <el-button 
              type="primary" 
              class="w-full !rounded-lg !h-11"
              :loading="sending"
              :disabled="!form.deviceId || !form.command"
              @click="handleSend"
            >
              <template #icon>
                <div class="i-solar-play-bold mr-1"></div>
              </template>
              发送指令
            </el-button>
          </el-form>
        </div>
        
        <!-- Status Tracking Card -->
        <div v-if="trackingCommand" class="bg-white rounded-2xl shadow-sm border border-white/60 p-6">
          <h4 class="text-sm font-semibold text-slate-700 mb-4 flex items-center gap-2">
            <div class="i-solar-refresh-circle-linear animate-spin text-blue-500" v-if="polling"></div>
            <div class="i-solar-check-circle-bold text-green-500" v-else-if="trackingCommand.status === CommandStatus.SUCCESS"></div>
            <div class="i-solar-close-circle-bold text-red-500" v-else-if="trackingCommand.status >= CommandStatus.FAILED"></div>
            <div class="i-solar-clock-circle-linear text-orange-500" v-else></div>
            指令状态追踪
          </h4>
          
          <div class="space-y-3 text-sm">
            <div class="flex justify-between">
              <span class="text-slate-500">请求ID</span>
              <span class="font-mono text-xs text-slate-600">{{ trackingCommand.requestId?.substring(0, 16) }}...</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-slate-500">状态</span>
              <el-tag :type="CommandStatusTagType[trackingCommand.status || 0]" size="small">
                {{ CommandStatusText[trackingCommand.status || 0] }}
              </el-tag>
            </div>
            <div class="flex justify-between">
              <span class="text-slate-500">发送时间</span>
              <span class="text-slate-600">{{ formatTime(trackingCommand.sentAt) }}</span>
            </div>
            <div v-if="trackingCommand.errorMsg" class="mt-2 p-2 bg-red-50 rounded-lg text-red-600 text-xs">
              {{ trackingCommand.errorMsg }}
            </div>
          </div>
        </div>
      </div>
      
      <!-- Right: Command History -->
      <div class="lg:col-span-8 bg-white rounded-2xl shadow-sm border border-white/60 flex flex-col">
        <div class="p-5 border-b border-slate-100 flex items-center justify-between shrink-0">
          <h3 class="text-lg font-semibold text-slate-800">指令历史</h3>
          <div class="flex items-center gap-3">
            <el-select 
              v-model="filterDeviceId" 
              placeholder="筛选设备" 
              clearable 
              style="width: 180px"
              @change="fetchHistory"
            >
              <el-option 
                v-for="device in controllableDevices" 
                :key="device.deviceId" 
                :label="device.name || device.deviceId"
                :value="device.deviceId"
              />
            </el-select>
            <el-button :icon="Refresh" circle @click="fetchHistory" />
          </div>
        </div>
        
        <div class="flex-1 overflow-auto p-5">
          <el-table 
            :data="historyList" 
            style="width: 100%"
            :header-cell-style="{ background: '#F8FAFC', color: '#475569' }"
          >
            <el-table-column prop="deviceId" label="设备ID" width="140">
              <template #default="{ row }">
                <span class="font-mono text-xs">{{ row.deviceId }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="command" label="指令" width="100">
              <template #default="{ row }">
                <el-tag size="small" effect="plain">{{ row.command }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="params" label="参数" min-width="150">
              <template #default="{ row }">
                <span class="text-xs text-slate-500 truncate block max-w-[200px]" :title="row.params">
                  {{ row.params || '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="CommandStatusTagType[row.status]" size="small">
                  {{ CommandStatusText[row.status] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sentAt" label="发送时间" width="170">
              <template #default="{ row }">
                <span class="text-xs text-slate-500">{{ formatTime(row.sentAt) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="90" fixed="right">
              <template #default="{ row }">
                <el-button 
                  v-if="row.status === CommandStatus.SENT"
                  link 
                  type="primary" 
                  size="small"
                  @click="refreshStatus(row.requestId)"
                >
                  刷新状态
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="p-4 border-t border-slate-100 flex justify-end shrink-0">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="historyTotal"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
          />
        </div>
      </div>
      
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { deviceApi, type Device } from '@/api/device'
import { controlApi, type ControlCommand, type SendCommandResponse } from '@/api/control'
import { CommandStatus, CommandStatusText, CommandStatusTagType } from '@/constants/command'

// Form state
const form = reactive({
  deviceId: '',
  command: '',
  params: ''
})

// Devices
const allDevices = ref<Device[]>([])

// Controllable devices (non-sensor types)
const controllableDevices = computed(() => {
  return allDevices.value.filter(d => !d.deviceType?.includes('SENSOR'))
})

// History
const historyList = ref<ControlCommand[]>([])
const historyTotal = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const filterDeviceId = ref('')

// Sending & Tracking
const sending = ref(false)
const trackingCommand = ref<SendCommandResponse | null>(null)
const polling = ref(false)
let pollTimer: number | null = null

// Fetch devices
const fetchDevices = async () => {
  try {
    const res = await deviceApi.getPage(1, 100)
    allDevices.value = res.records || []
  } catch (e) {
    console.error('Failed to fetch devices', e)
  }
}

// Fetch history
const fetchHistory = async () => {
  try {
    const res = await controlApi.getPage(currentPage.value, pageSize.value, filterDeviceId.value || undefined)
    historyList.value = res.records || []
    historyTotal.value = res.total || 0
  } catch (e) {
    console.error('Failed to fetch history', e)
  }
}

// Handle page change
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchHistory()
}

// Send command
const handleSend = async () => {
  if (!form.deviceId || !form.command) {
    ElMessage.warning('请选择设备和指令类型')
    return
  }
  
  // Validate params JSON if provided
  if (form.params) {
    try {
      JSON.parse(form.params)
    } catch {
      ElMessage.error('参数格式错误，请输入有效的 JSON')
      return
    }
  }
  
  sending.value = true
  try {
    const res = await controlApi.send({
      deviceId: form.deviceId,
      command: form.command,
      params: form.params || undefined
    })
    
    ElMessage.success('指令已发送')
    trackingCommand.value = res
    
    // Start polling status
    startPolling(res.requestId)
    
    // Refresh history
    fetchHistory()
    
    // Reset form
    form.command = ''
    form.params = ''
  } catch (e) {
    ElMessage.error('发送失败')
    console.error(e)
  } finally {
    sending.value = false
  }
}

// Poll status
const startPolling = (requestId: string) => {
  if (pollTimer) {
    clearInterval(pollTimer)
  }
  
  polling.value = true
  let attempts = 0
  const maxAttempts = 15 // 30 seconds max (2s interval)
  
  pollTimer = window.setInterval(async () => {
    attempts++
    try {
      const res = await controlApi.getStatus(requestId)
      trackingCommand.value = res
      
      // Stop polling if terminal state reached
      if (res.status >= CommandStatus.SUCCESS) {
        stopPolling()
        fetchHistory() // Refresh list
      }
    } catch (e) {
      console.error('Failed to poll status', e)
    }
    
    // Stop after max attempts
    if (attempts >= maxAttempts) {
      stopPolling()
    }
  }, 2000)
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
  polling.value = false
}

// Refresh single command status
const refreshStatus = async (requestId: string) => {
  try {
    const res = await controlApi.getStatus(requestId)
    // Update in list
    const idx = historyList.value.findIndex((c: ControlCommand) => c.requestId === requestId)
    if (idx !== -1) {
      historyList.value[idx].status = res.status
      historyList.value[idx].statusText = res.statusText
    }
    ElMessage.success('状态已刷新')
  } catch (e) {
    ElMessage.error('刷新失败')
  }
}

// Format time helper
const formatTime = (time?: string) => {
  if (!time) return '-'
  return time.substring(0, 19).replace('T', ' ')
}

// Lifecycle
onMounted(() => {
  fetchDevices()
  fetchHistory()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #475569;
}
</style>
