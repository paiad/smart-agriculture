<template>
  <div class="bg-white rounded-xl border border-slate-100 shadow-sm min-h-[calc(100vh-140px)] flex flex-col">
    <!-- Toolbar -->
    <div class="p-5 border-b border-slate-100 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <h2 class="text-lg font-semibold text-slate-800">设备管理</h2>
        <div class="h-4 w-[1px] bg-slate-200"></div>
        <div class="flex items-center gap-2">
            <el-input placeholder="搜索设备ID/名称..." prefix-icon="Search" style="width: 240px" />
            <el-select placeholder="设备状态" style="width: 120px">
                <el-option label="在线" value="online" />
                <el-option label="离线" value="offline" />
            </el-select>
        </div>
      </div>
      <el-button type="primary" class="!rounded-lg !px-4" icon="Plus">
        新增设备
      </el-button>
    </div>

    <!-- Table -->
    <div class="flex-1 p-5">
      <el-table :data="tableData" style="width: 100%" :header-cell-style="{ background: '#F8FAFC', color: '#475569' }">
        <el-table-column prop="deviceId" label="设备ID" width="180" />
        <el-table-column prop="deviceName" label="设备名称" width="200" />
        <el-table-column prop="deviceType" label="类型" width="120">
            <template #default="{ row }">
                <span class="px-2 py-1 bg-slate-100 text-slate-600 rounded text-xs">{{ row.deviceType }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
                <div class="flex items-center gap-1.5">
                    <div class="w-1.5 h-1.5 rounded-full" :class="row.status === 1 ? 'bg-green-500' : 'bg-slate-300'"></div>
                    <span :class="row.status === 1 ? 'text-green-600' : 'text-slate-400'">{{ row.status === 1 ? '在线' : '离线' }}</span>
                </div>
            </template>
        </el-table-column>
        <el-table-column prop="location" label="安装位置" />
        <el-table-column prop="lastActiveTime" label="最后活跃" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default>
            <el-button link type="primary" size="small">编辑</el-button>
            <el-button link type="danger" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="mt-4 flex justify-end">
        <el-pagination 
            background 
            layout="prev, pager, next" 
            :total="total" 
            :page-size="pageSize"
            @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { deviceApi, type Device, type PageResult } from '@/api/device'
import { ElMessage } from 'element-plus'

const tableData = ref<Device[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const statusFilter = ref('')
const searchKeyword = ref('')

const fetchDevices = async () => {
    try {
        // TODO: Handle search and filter in backend if needed, currently API only supports basic pagination
        // For now we just fetch page. Ideally backend should support query params.
        const res = await deviceApi.getPage(currentPage.value, pageSize.value)
        tableData.value = res.records
        total.value = res.total
    } catch (e) {
        console.error(e)
    }
}

const handlePageChange = (page: number) => {
    currentPage.value = page
    fetchDevices()
}

// Initial fetch
onMounted(() => fetchDevices())
</script>
