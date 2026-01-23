<template>
  <div class="bg-white rounded-2xl border border-slate-100 shadow-sm min-h-[calc(100vh-140px)] flex flex-col">
    <!-- Toolbar -->
    <div class="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
      <div class="flex items-center gap-5">
        <h2 class="text-lg font-bold text-slate-800 tracking-tight">设备管理</h2>
        <div class="h-5 w-[1px] bg-slate-200"></div>
        <div class="flex items-center gap-3">
          <el-input 
            v-model="searchKeyword"
            placeholder="搜索设备ID或名称..." 
            prefix-icon="Search" 
            class="w-[280px] apple-input-search"
            clearable
            @input="handleSearch"
          />
          <el-select 
            v-model="statusFilter" 
            placeholder="状态" 
            class="w-[120px]"
            clearable
            @change="handleSearch"
          >
            <el-option label="在线" :value="1">
              <span class="flex items-center gap-2">
                <span class="w-1.5 h-1.5 rounded-full bg-green-500"></span>
                <span>在线</span>
              </span>
            </el-option>
            <el-option label="离线" :value="0">
              <span class="flex items-center gap-2">
                <span class="w-1.5 h-1.5 rounded-full bg-slate-300"></span>
                <span>离线</span>
              </span>
            </el-option>
          </el-select>
        </div>
      </div>
      <el-button 
        type="primary" 
        class="!rounded-xl !px-5 !h-[40px] !bg-brand-600 !border-brand-600 hover:!bg-brand-700 shadow-sm transition-all" 
        icon="Plus" 
        @click="openDialog()"
      >
        新增设备
      </el-button>
    </div>

    <!-- Table -->
    <div class="flex-1 p-6">
      <el-table 
        :data="filteredData" 
        style="width: 100%" 
        :header-cell-style="{ background: '#F8FAFC', color: '#64748B', fontWeight: '600' }"
        v-loading="loading"
      >
        <el-table-column prop="deviceId" label="设备ID" width="210">
          <template #default="{ row }">
            <span class="font-mono text-xs text-slate-600">{{ row.deviceId }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="设备名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="deviceType" label="类型" width="180">
          <template #default="{ row }">
            <span class="px-2 py-0.5 bg-slate-100 text-slate-600 rounded text-xs whitespace-nowrap">{{ row.deviceType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="online" label="状态" width="160">
          <template #default="{ row }">
            <div class="flex items-center gap-1.5">
              <div class="w-1.5 h-1.5 rounded-full" :class="row.online === 1 ? 'bg-green-500' : 'bg-slate-300'"></div>
              <span :class="row.online === 1 ? 'text-green-600' : 'text-slate-400'">{{ row.online === 1 ? '在线' : '离线' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="locationDesc" label="安装位置" min-width="200" show-overflow-tooltip />
        <el-table-column prop="lastSeenAt" label="最后活跃" width="170">
          <template #default="{ row }">
            <span class="text-xs text-slate-500">{{ formatTime(row.lastSeenAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="mt-6 flex justify-end">
        <el-pagination 
          background 
          layout="total, prev, pager, next" 
          :total="total" 
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="editingDevice ? '编辑设备' : '新增设备'"
      width="520px"
      align-center
      destroy-on-close
      class="rounded-2xl overflow-hidden"
    >
      <el-form :model="formData" label-position="top" class="p-2 space-y-4">
        <div class="grid grid-cols-2 gap-4">
             <el-form-item label="设备ID" required>
              <el-input 
                v-model="formData.deviceId" 
                placeholder="例如: SENSOR001"
                :disabled="!!editingDevice"
              />
            </el-form-item>
             <el-form-item label="设备类型" required>
              <el-select v-model="formData.deviceType" placeholder="选择类型" class="w-full">
                <el-option label="七合一传感器" value="SENSOR_7IN1" />
                <el-option label="气象站" value="WEATHER_STATION" />
                <el-option label="风扇" value="FAN" />
                <el-option label="水泵" value="PUMP" />
                <el-option label="卷帘机" value="ROLLER" />
                <el-option label="补光灯" value="LIGHT" />
              </el-select>
            </el-form-item>
        </div>
       
        <el-form-item label="设备名称">
          <el-input v-model="formData.name" placeholder="例如: 一号大棚温湿度传感器" />
        </el-form-item>
        
        <el-form-item label="安装位置">
          <el-input v-model="formData.locationDesc" placeholder="例如: 一号大棚东侧" />
        </el-form-item>
        <el-form-item label="运行状态">
          <el-radio-group v-model="formData.status" size="large">
             <el-radio-button :value="1">启用</el-radio-button>
             <el-radio-button :value="0">停用</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="可选备注信息" resize="none" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex gap-3 justify-end pt-2">
           <el-button @click="dialogVisible = false" class="!rounded-lg !px-5">取消</el-button>
           <el-button type="primary" :loading="submitting" @click="handleSubmit" class="!rounded-lg !px-6 !bg-brand-600 !border-brand-600">
             {{ editingDevice ? '保存更改' : '立即创建' }}
           </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { deviceApi, type Device } from '@/api/device'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref<Device[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const statusFilter = ref<number | ''>('')
const searchKeyword = ref('')
const loading = ref(false)

// Dialog
const dialogVisible = ref(false)
const editingDevice = ref<Device | null>(null)
const submitting = ref(false)
const formData = ref({
  deviceId: '',
  name: '',
  deviceType: '',
  locationDesc: '',
  status: 1,
  remark: ''
})

// Filtered data (client-side filtering for search/status)
const filteredData = computed(() => {
  let data = tableData.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    data = data.filter(d => 
      d.deviceId?.toLowerCase().includes(keyword) || 
      d.name?.toLowerCase().includes(keyword)
    )
  }
  
  if (statusFilter.value !== '') {
    data = data.filter(d => d.online === statusFilter.value)
  }
  
  return data
})

const fetchDevices = async () => {
  loading.value = true
  try {
    const res = await deviceApi.getPage(currentPage.value, pageSize.value)
    tableData.value = res.records
    total.value = res.total
  } catch (e) {
    console.error(e)
    ElMessage.error('获取设备列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchDevices()
}

const handleSearch = () => {
  // Client-side filtering, no need to refetch
}

const openDialog = (device?: Device) => {
  if (device) {
    editingDevice.value = device
    formData.value = {
      deviceId: device.deviceId,
      name: device.name || '',
      deviceType: device.deviceType || '',
      locationDesc: device.locationDesc || '',
      status: device.status ?? 1,
      remark: device.remark || ''
    }
  } else {
    editingDevice.value = null
    formData.value = {
      deviceId: '',
      name: '',
      deviceType: '',
      locationDesc: '',
      status: 1,
      remark: ''
    }
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formData.value.deviceId || !formData.value.deviceType) {
    ElMessage.warning('请填写必填项')
    return
  }
  
  submitting.value = true
  try {
    if (editingDevice.value) {
      await deviceApi.update({
        id: editingDevice.value.id,
        ...formData.value
      })
      ElMessage.success('设备已更新')
    } else {
      await deviceApi.save(formData.value)
      ElMessage.success('设备已创建')
    }
    dialogVisible.value = false
    fetchDevices()
  } catch (e) {
    ElMessage.error('操作失败')
    console.error(e)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (device: Device) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除设备 "${device.name || device.deviceId}" 吗？此操作不可恢复。`,
      '删除确认',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消', confirmButtonClass: 'el-button--danger' }
    )
    
    await deviceApi.delete(device.id)
    ElMessage.success('设备已删除')
    fetchDevices()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(e)
    }
  }
}

const formatTime = (time?: string) => {
  if (!time) return '-'
  return time.substring(0, 19).replace('T', ' ')
}

onMounted(() => fetchDevices())
</script>

<style scoped>
:deep(.el-table) {
  --el-table-border-color: #F1F5F9;
  --el-table-header-bg-color: #F8FAFC;
}

:deep(.apple-input-search .el-input__wrapper) {
  background-color: #F1F5F9 !important;
  box-shadow: none !important;
  transition: all 0.2s;
}
:deep(.apple-input-search .el-input__wrapper:hover),
:deep(.apple-input-search .el-input__wrapper.is-focus) {
  background-color: #fff !important;
  box-shadow: 0 0 0 1px #CBD5E1 inset !important;
}
</style>
