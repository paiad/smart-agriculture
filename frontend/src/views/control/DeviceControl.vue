<template>
  <div class="flex h-[calc(100vh-140px)] bg-[#F5F5F7] p-4 gap-4 overflow-hidden">
    <!-- Left: Device List -->
    <aside class="w-[320px] bg-white rounded-xl shadow-sm border border-slate-100 flex flex-col shrink-0 overflow-hidden">
      <!-- Header & Search -->
      <div class="px-4 py-3 border-b border-slate-50">
        <div class="flex items-center justify-between mb-3">
          <h3 class="text-[15px] font-semibold text-slate-800">设备列表</h3>
          <span class="px-2 py-0.5 text-[11px] font-medium text-slate-500 bg-slate-100 rounded-full">{{ allDevices.length }} 台</span>
        </div>
        <div class="relative group">
           <div class="absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors">
            <div class="i-solar-magnifer-linear text-sm"></div>
          </div>
          <input 
            v-model="searchQuery"
            type="text"
            placeholder="搜索设备..."
            class="w-full bg-[#F5F7FA] border-none rounded-lg py-1.5 pl-8 pr-3 text-sm focus:outline-none focus:ring-1 focus:ring-blue-500/50 focus:bg-white transition-all placeholder:text-slate-400"
          >
          <button 
            v-if="searchQuery"
            @click="searchQuery = ''"
            class="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
          >
            <div class="i-solar-close-circle-bold text-xs"></div>
          </button>
        </div>
      </div>
      
      <!-- List -->
      <div class="flex-1 overflow-y-auto p-2 space-y-1">
        <div
          v-for="device in filteredDevices"
          :key="device.deviceId"
          class="flex items-center p-3 rounded-lg cursor-pointer transition-all border border-transparent group hover:bg-slate-50 hover:border-slate-100"
          :class="selectedDevice?.deviceId === device.deviceId ? '!bg-blue-50 !border-blue-100 shadow-sm' : ''"
          @click="selectDevice(device)"
        >
          <!-- Status Dot -->
          <div class="relative shrink-0 mr-3">
            <div 
              class="w-2.5 h-2.5 rounded-full ring-2 ring-white shadow-sm"
              :class="getStatusColor(device.online)"
            ></div>
          </div>
          
          <!-- Device Info -->
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between mb-0.5">
              <span 
                class="text-[14px] font-medium truncate"
                :class="selectedDevice?.deviceId === device.deviceId ? 'text-blue-700' : 'text-slate-700'"
              >
                {{ device.name || device.deviceId }}
              </span>
            </div>
            <div class="flex items-center text-[11px] text-slate-400 gap-2">
              <span class="truncate font-mono">{{ device.deviceId }}</span>
              <span class="bg-slate-100 px-1.5 py-0.5 rounded text-slate-500 scale-95 origin-left">{{ device.deviceType }}</span>
            </div>
          </div>
          
          <!-- Arrow -->
          <div 
            class="i-solar-alt-arrow-right-linear text-slate-300 text-sm opacity-0 -translate-x-2 transition-all group-hover:opacity-100 group-hover:translate-x-0"
            :class="selectedDevice?.deviceId === device.deviceId ? '!opacity-100 !translate-x-0 !text-blue-400' : ''"
          ></div>
        </div>
        
        <div v-if="filteredDevices.length === 0" class="flex flex-col items-center justify-center py-10 text-slate-400">
          <div class="i-solar-box-minimalistic-linear text-3xl mb-2 opacity-50"></div>
          <p class="text-xs">未找到匹配设备</p>
        </div>
      </div>
    </aside>
    
    <!-- Right: Control Panel (Main Area) -->
    <main class="flex-1 bg-white rounded-xl shadow-sm border border-slate-100 flex flex-col overflow-hidden relative">
      <!-- No device selected -->
      <div v-if="!selectedDevice" class="absolute inset-0 flex flex-col items-center justify-center text-slate-400 bg-white z-10">
        <div class="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mb-4">
          <div class="i-solar-devices-linear text-3xl opacity-50"></div>
        </div>
        <h3 class="text-lg font-medium text-slate-700 mb-1">选择设备开始控制</h3>
        <p class="text-sm">请从左侧列表选择一个在线设备进行操作</p>
      </div>
      
      <div v-else class="flex flex-col h-full bg-[#FAFAFA]">
        <!-- Device Header -->
        <header class="bg-white px-6 py-4 border-b border-slate-100 shadow-[0_1px_2px_rgba(0,0,0,0.02)] shrink-0 z-10 flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-center text-2xl shadow-sm">
              <span v-if="selectedDevice.deviceType?.includes('SENSOR')">🌡️</span>
              <span v-else-if="selectedDevice.deviceType?.includes('FAN')">🌀</span>
              <span v-else-if="selectedDevice.deviceType?.includes('PUMP')">💧</span>
              <span v-else-if="selectedDevice.deviceType?.includes('LIGHT')">💡</span>
              <span v-else>📡</span>
            </div>
            <div>
              <div class="flex items-center gap-2 mb-1">
                <h2 class="text-lg font-bold text-slate-800">{{ selectedDevice.name || selectedDevice.deviceId }}</h2>

                <span 
                  v-if="selectedDevice.online"
                  class="px-2 py-0.5 text-[10px] font-bold rounded-full border shadow-sm uppercase tracking-wide"
                  :class="selectedDevice.running 
                    ? 'bg-emerald-500 text-white border-emerald-600 animate-pulse' 
                    : 'bg-slate-200 text-slate-600 border-slate-300'"
                >
                  {{ selectedDevice.running ? '● RUNNING' : '○ STANDBY' }}
                </span>
              </div>
              <div class="flex items-center gap-3 text-xs text-slate-500 font-mono">
                <span>ID: {{ selectedDevice.deviceId }}</span>
                <span class="w-px h-3 bg-slate-200"></span>
                <span>TYPE: {{ selectedDevice.deviceType }}</span>
              </div>
            </div>
          </div>
          
          <div class="flex items-center gap-3">
             <button 
               class="relative z-50 w-10 h-10 flex items-center justify-center rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-600 transition-colors border-0 outline-none ring-0 pointer-events-auto"
               @click.stop="handleMoreActions"
             >
               <div class="i-solar-menu-dots-bold rotate-90 pointer-events-none"></div>
             </button>
          </div>
        </header>

        <!-- Scrollable Content Area -->
        <div class="flex-1 overflow-y-auto p-6 space-y-6 scrollbar-thin">
          
          <!-- Control Section -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6 items-start">
            
            <!-- Quick Actions -->
            <div class="bg-white rounded-xl shadow-[0_2px_8px_rgba(0,0,0,0.04)] ring-1 ring-black/5 overflow-hidden">
              <div class="px-4 py-3 border-b border-slate-50 bg-slate-50/50 flex justify-between items-center">
                <h4 class="text-xs font-bold text-slate-500 uppercase tracking-wider">Control Actions</h4>
                <div class="text-[10px] text-slate-400 bg-white px-2 py-0.5 rounded border border-slate-100">
                  {{ selectedDevice.online ? 'Ready to command' : 'Device offline' }}
                </div>
              </div>
              
              <div class="p-4 grid grid-cols-2 gap-3">
                <button 
                  class="group relative flex flex-col items-center justify-center py-6 rounded-xl border border-emerald-100 bg-gradient-to-br from-white to-emerald-50/50 hover:to-emerald-100/50 hover:border-emerald-200 hover:shadow-md transition-all duration-200 disabled:opacity-50 disabled:grayscale focus:outline-none focus:ring-2 focus:ring-emerald-500/30"
                  :disabled="sending"
                  @click="quickSend('ON')"
                >
                  <div class="w-10 h-10 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-600 mb-3 group-hover:scale-110 transition-transform shadow-sm">
                    <div class="i-solar-power-bold text-lg"></div>
                  </div>
                  <span class="text-sm font-semibold text-slate-700 group-hover:text-emerald-700">开启</span>
                </button>
                
                <button 
                  class="group relative flex flex-col items-center justify-center py-6 rounded-xl border border-rose-100 bg-gradient-to-br from-white to-rose-50/50 hover:to-rose-100/50 hover:border-rose-200 hover:shadow-md transition-all duration-200 disabled:opacity-50 disabled:grayscale focus:outline-none focus:ring-2 focus:ring-rose-500/30"
                  :disabled="sending"
                  @click="quickSend('OFF')"
                >
                  <div class="w-10 h-10 rounded-full bg-rose-100 flex items-center justify-center text-rose-600 mb-3 group-hover:scale-110 transition-transform shadow-sm">
                    <div class="i-solar-power-linear text-lg"></div>
                  </div>
                  <span class="text-sm font-semibold text-slate-700 group-hover:text-rose-700">关闭</span>
                </button>
                
                <button 
                  class="group relative flex flex-col items-center justify-center py-5 rounded-xl border border-amber-100 bg-gradient-to-br from-white to-amber-50/50 hover:to-amber-100/50 hover:border-amber-200 hover:shadow-md transition-all duration-200 disabled:opacity-50 disabled:grayscale focus:outline-none focus:ring-2 focus:ring-amber-500/30"
                  :disabled="sending"
                  @click="quickSend('RESTART')"
                >
                  <div class="w-8 h-8 rounded-full bg-amber-100 flex items-center justify-center text-amber-600 mb-2 group-hover:scale-110 transition-transform">
                    <div class="i-solar-refresh-bold text-base"></div>
                  </div>
                  <span class="text-xs font-semibold text-slate-600 group-hover:text-amber-700">重启设备</span>
                </button>

                 <div class="relative">
                  <button 
                    class="w-full h-full group flex flex-col items-center justify-center py-5 rounded-xl border border-blue-100 bg-gradient-to-br from-white to-blue-50/50 hover:to-blue-100/50 hover:border-blue-200 hover:shadow-md transition-all duration-200 disabled:opacity-50 disabled:grayscale focus:outline-none focus:ring-2 focus:ring-blue-500/30"
                    :disabled="sending"
                    @click="showAdvanced = !showAdvanced"
                    :class="showAdvanced ? '!bg-blue-50 !border-blue-300 ring-2 ring-blue-500/20' : ''"
                  >
                    <div class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 mb-2 group-hover:scale-110 transition-transform">
                      <div class="i-solar-settings-bold text-base"></div>
                    </div>
                    <span class="text-xs font-semibold text-slate-600 group-hover:text-blue-700">参数配置</span>
                  </button>
                 </div>
              </div>
            </div>

            <!-- Advanced Config Panel -->
             <div 
              v-if="showAdvanced" 
              class="bg-white rounded-xl shadow-[0_2px_8px_rgba(0,0,0,0.04)] ring-1 ring-black/5 overflow-hidden animate-in fade-in slide-in-from-top-4 duration-200"
            >
              <div class="px-4 py-3 border-b border-slate-50 bg-blue-50/30 flex justify-between items-center">
                <h4 class="text-xs font-bold text-blue-600 uppercase tracking-wider flex items-center gap-1.5">
                  <div class="i-solar-tuning-bold"></div>
                  Advanced Config
                </h4>
                <button @click="showAdvanced = false" class="text-slate-400 hover:text-slate-600 focus:outline-none focus:ring-2 focus:ring-slate-500/20 rounded">
                  <div class="i-solar-close-circle-bold"></div>
                </button>
              </div>
              <div class="p-4 space-y-3">
                <div class="relative">
                  <textarea 
                    v-model="advancedParams"
                    rows="4" 
                    placeholder='{"speed": 100, "mode": "ECO"}'
                    class="w-full p-3 font-mono text-xs bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all text-slate-700 resize-none"
                  ></textarea>
                  <div class="absolute right-2 bottom-2 text-[10px] text-slate-400 uppercase font-bold">JSON</div>
                </div>
                <div class="flex justify-end">
                  <button 
                    class="px-4 py-2 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-semibold rounded-lg shadow-sm shadow-blue-500/30 transition-all flex items-center gap-2"
                    :disabled="!advancedParams || sending"
                    @click="sendWithParams"
                  >
                    <div class="i-solar-plain-linear" v-if="!sending"></div>
                    <div class="i-solar-refresh-circle-linear animate-spin" v-else></div>
                    发送指令
                  </button>
                </div>
              </div>
            </div>

            <!-- Status Tracking (Only shows when active) -->
            <div v-if="trackingCommand" class="md:col-span-2">
               <div 
                class="rounded-xl border p-4 flex items-center gap-4 transition-all"
                :class="getTrackingStyles(trackingCommand.status)"
              >
                <div class="w-10 h-10 rounded-full flex items-center justify-center text-xl shrink-0 bg-white/80 shadow-sm backdrop-blur-sm">
                   <div v-if="polling" class="i-solar-refresh-circle-linear animate-spin text-blue-500"></div>
                   <div v-else-if="trackingCommand.status === CommandStatus.SUCCESS" class="i-solar-check-circle-bold text-emerald-500"></div>
                   <div v-else-if="trackingCommand.status >= CommandStatus.FAILED" class="i-solar-close-circle-bold text-rose-500"></div>
                   <div v-else class="i-solar-clock-circle-linear text-amber-500"></div>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 mb-0.5">
                    <span class="font-bold text-sm text-slate-700">
                      {{ polling ? 'Waiting for device response...' : CommandStatusText[trackingCommand.status || 0] }}
                    </span>
                  </div>
                  <div class="text-xs text-slate-500 font-mono flex items-center gap-2">
                    <span class="opacity-70">REQ: {{ trackingCommand.requestId?.substring(0, 16) }}...</span>
                  </div>
                </div>
                 <div v-if="trackingCommand.errorMsg" class="px-3 py-1 bg-white/50 rounded text-xs text-rose-600 font-medium">
                  {{ trackingCommand.errorMsg }}
                </div>
              </div>
            </div>

          </div>

          <!-- History Section -->
          <div class="bg-white rounded-xl shadow-[0_2px_8px_rgba(0,0,0,0.04)] ring-1 ring-black/5 flex flex-col">
            <div class="px-5 py-3 border-b border-slate-100 flex items-center justify-between">
              <h4 class="text-sm font-bold text-slate-800 flex items-center gap-2">
                <div class="i-solar-history-bold text-slate-400"></div>
                Command History
              </h4>
              <button 
                @click.stop="fetchHistory" 
                class="relative z-50 p-2 hover:bg-slate-100 rounded-md transition-colors text-slate-400 hover:text-blue-500 border-0 outline-none ring-0 pointer-events-auto"
              >
                <div class="i-solar-restart-linear pointer-events-none"></div>
              </button>
            </div>
            
            <div class="flex-1">
              <div v-if="deviceHistory.length > 0" class="divide-y divide-slate-50">
                 <div 
                  v-for="cmd in deviceHistory" 
                  :key="cmd.requestId" 
                  class="px-5 py-3 flex items-center hover:bg-slate-50 transition-colors group"
                >
                  <div class="w-24 shrink-0">
                    <div class="inline-flex items-center px-2 py-1 rounded-md text-[10px] uppercase font-bold tracking-wider" :class="getCmdBadgeStyle(cmd.command)">
                      {{ cmd.command }}
                    </div>
                  </div>
                  
                  <div class="flex-1 min-w-0 pr-4">
                     <div class="text-xs font-mono text-slate-500 truncate" :title="cmd.params">
                      {{ cmd.params || '-' }}
                     </div>
                  </div>
                  
                  <div class="w-20 shrink-0 text-right mr-6">
                    <span class="text-[11px] font-medium" :class="getStatusTextColor(cmd.status)">
                       {{ CommandStatusText[cmd.status ?? 0] }}
                    </span>
                  </div>
                  
                  <div class="w-32 shrink-0 text-right text-[11px] text-slate-400 font-mono">
                    {{ formatTime(cmd.sentAt) }}
                  </div>
                </div>
              </div>
              
               <div v-else class="py-12 flex flex-col items-center justify-center text-slate-300">
                  <div class="i-solar-file-text-linear text-3xl mb-2 opacity-50"></div>
                  <p class="text-xs">No command history</p>
               </div>
            </div>

             <div v-if="historyTotal > pageSize" class="px-5 py-3 border-t border-slate-50 flex justify-center">
                <el-pagination
                  small
                  background
                  layout="prev, pager, next"
                  :total="historyTotal"
                  :page-size="pageSize"
                  :current-page="currentPage"
                  @current-change="handlePageChange"
                  class="opacity-75 hover:opacity-100 transition-opacity"
                />
             </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { deviceApi, type Device } from '@/api/device'
import { controlApi, type ControlCommand, type SendCommandResponse } from '@/api/control'
import { CommandStatus, CommandStatusText } from '@/constants/command'

// Devices
const allDevices = ref<Device[]>([])
const selectedDevice = ref<Device | null>(null)
const searchQuery = ref('')

// Filtered devices
const filteredDevices = computed(() => {
  if (!searchQuery.value) return allDevices.value
  const q = searchQuery.value.toLowerCase()
  return allDevices.value.filter(d => 
    d.deviceId.toLowerCase().includes(q) || 
    d.name?.toLowerCase().includes(q) ||
    d.deviceType?.toLowerCase().includes(q)
  )
})

// UI State
const showAdvanced = ref(false)
const advancedParams = ref('')

// Sending & Tracking
const sending = ref(false)
const trackingCommand = ref<SendCommandResponse | null>(null)
const polling = ref(false)
let pollTimer: number | null = null

// History for selected device
const deviceHistory = ref<ControlCommand[]>([])
const historyTotal = ref(0)
const currentPage = ref(1)
const pageSize = ref(8)

// Fetch all devices
const fetchDevices = async () => {
  try {
    const res = await deviceApi.getPage(1, 100)
    allDevices.value = res.records || []
  } catch (e) {
    console.error('Failed to fetch devices', e)
  }
}

// Select device
const selectDevice = (device: Device) => {
  selectedDevice.value = device
  showAdvanced.value = false
  advancedParams.value = ''
  trackingCommand.value = null
  currentPage.value = 1
  fetchHistory()
}

// Fetch history for selected device
const fetchHistory = async () => {
  console.log('Fetching history...')
  if (!selectedDevice.value) return
  try {
    const res = await controlApi.getPage(currentPage.value, pageSize.value, selectedDevice.value.deviceId)
    deviceHistory.value = res.records || []
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

// Quick send command
const quickSend = async (command: string) => {
  if (!selectedDevice.value) return
  await sendCommand(command)
}

// Send with params
const sendWithParams = async () => {
  if (!selectedDevice.value || !advancedParams.value) return
  
  try {
    JSON.parse(advancedParams.value)
  } catch {
    ElMessage.error('Invalid JSON format')
    return
  }
  
  await sendCommand('SET', advancedParams.value)
}

// More Actions
const handleMoreActions = () => {
  console.log('Menu button clicked')
  ElMessage.info('更多操作功能正在开发中')
}

// Core send command
const sendCommand = async (command: string, params?: string) => {
  if (!selectedDevice.value) return
  
  sending.value = true
  try {
    const res = await controlApi.send({
      deviceId: selectedDevice.value.deviceId,
      command,
      params
    })
    
    ElMessage.success('Command sent successfully')
    trackingCommand.value = res
    startPolling(res.requestId)
    fetchHistory() // Optimistic update
    
    if (command === 'SET') {
      advancedParams.value = ''
      showAdvanced.value = false
    }
  } catch (e) {
    ElMessage.error('Failed to send command')
    console.error(e)
  } finally {
    sending.value = false
  }
}

// Poll status
const startPolling = (requestId: string) => {
  if (pollTimer) clearInterval(pollTimer)
  
  polling.value = true
  let attempts = 0
  const maxAttempts = 15
  
  pollTimer = window.setInterval(async () => {
    attempts++
    try {
      const res = await controlApi.getStatus(requestId)
      trackingCommand.value = res
      
      if (res.status >= CommandStatus.SUCCESS) {
        stopPolling()
        fetchHistory()
      }
    } catch (e) {
      console.error('Polling error', e)
    }
    
    if (attempts >= maxAttempts) stopPolling()
  }, 2000)
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
  polling.value = false
}

// Styling Helpers
const getStatusColor = (online?: number) => {
  return online === 1 ? 'bg-emerald-500' : 'bg-slate-300'
}



const getTrackingStyles = (status?: number) => {
  if (status === CommandStatus.SUCCESS) return 'bg-emerald-50 border-emerald-100'
  if (status && status >= CommandStatus.FAILED) return 'bg-rose-50 border-rose-100'
  return 'bg-amber-50 border-amber-100' // Pending
}

const getCmdBadgeStyle = (cmd: string) => {
  if (cmd === 'ON') return 'bg-emerald-100 text-emerald-700 border border-emerald-200'
  if (cmd === 'OFF') return 'bg-rose-100 text-rose-700 border border-rose-200'
  if (cmd === 'RESTART') return 'bg-amber-100 text-amber-700 border border-amber-200'
  return 'bg-blue-100 text-blue-700 border border-blue-200'
}

const getStatusTextColor = (status?: number) => {
  if (status === CommandStatus.SUCCESS) return 'text-emerald-600'
  if (status && status >= CommandStatus.FAILED) return 'text-rose-600'
  return 'text-amber-600'
}

const formatTime = (time?: string) => {
  if (!time) return '-'
  return time.substring(5, 19).replace('T', ' ')
}

// Watch selected device changes
watch(selectedDevice, () => {
  stopPolling()
  trackingCommand.value = null
})

// Lifecycle
onMounted(() => {
  fetchDevices()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
/* 深度抑制任何按钮的默认边框和点击框线 */
button {
  border: none !important;
  outline: none !important;
  -webkit-tap-highlight-color: transparent;
}

button:focus,
button:active,
button:focus-visible {
  outline: none !important;
  border: none !important;
  box-shadow: none !important;
}

/* 重新实现 ring 效果，通过自定义 class 确保不被覆盖 */
.custom-ring:focus {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3) !important;
}

/* 针对带有 i-solar 图标的按钮进行优化 */
[class^="i-solar"] {
  display: inline-block;
}

/* Custom Scrollbar for this component */
.scrollbar-thin::-webkit-scrollbar {
  width: 6px;
}
.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background-color: #E2E8F0;
  border-radius: 20px;
}
</style>
