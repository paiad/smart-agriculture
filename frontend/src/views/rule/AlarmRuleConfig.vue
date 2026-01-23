<template>
  <div class="bg-white rounded-xl border border-slate-100 shadow-sm min-h-[calc(100vh-140px)] p-5">
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-lg font-semibold text-slate-800">告警规则配置</h2>
      <el-button type="primary" icon="Plus" @click="openDialog()">新增规则</el-button>
    </div>

    <div v-if="rules.length === 0 && !loading" class="flex flex-col items-center justify-center py-20">
      <div class="w-20 h-20 rounded-full bg-slate-50 flex items-center justify-center mb-4">
        <div class="i-solar-settings-linear text-3xl text-slate-300"></div>
      </div>
      <p class="text-slate-500 mb-4">暂无告警规则</p>
      <el-button type="primary" @click="openDialog()">创建第一条规则</el-button>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5" v-loading="loading">
      <div 
        v-for="rule in rules" 
        :key="rule.id" 
        class="border border-slate-200 rounded-lg p-4 hover:border-brand-400 hover:shadow-sm transition-all cursor-pointer relative group"
      >
        <div class="absolute top-3 right-3 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
          <el-button circle size="small" :icon="Edit" @click="openDialog(rule)" />
          <el-button circle size="small" type="danger" :icon="Delete" @click="handleDelete(rule)" />
        </div>
        
        <div class="flex items-center gap-2 mb-3">
          <span class="font-semibold text-slate-700">{{ rule.ruleName || `规则 #${rule.id}` }}</span>
          <el-switch 
            v-model="rule.enabled" 
            :active-value="1" 
            :inactive-value="0"
            size="small"
            @change="handleToggle(rule)"
          />
        </div>
        
        <div class="space-y-2 text-sm">
          <div class="flex justify-between">
            <span class="text-slate-500">适用设备:</span>
            <span class="font-medium text-slate-700">{{ rule.deviceId || rule.deviceType || '通用' }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-slate-500">监测指标:</span>
            <span class="font-medium text-slate-700">{{ getMetricLabel(rule.metric) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-slate-500">阈值条件:</span>
            <span class="font-medium text-red-500">
              {{ formatThreshold(rule) }}
            </span>
          </div>
          <div class="flex justify-between">
            <span class="text-slate-500">告警级别:</span>
            <el-tag :type="getLevelType(rule.level)" size="small" effect="plain">
              {{ rule.level || 'medium' }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="editingRule ? '编辑规则' : '新增规则'"
      width="520px"
      destroy-on-close
    >
      <el-form :model="formData" label-width="100px" class="pr-4">
        <el-form-item label="规则名称">
          <el-input v-model="formData.ruleName" placeholder="例如: 温度过高告警" />
        </el-form-item>
        <el-form-item label="适用范围">
          <el-radio-group v-model="scopeType" class="mb-2">
            <el-radio label="global">全局规则</el-radio>
            <el-radio label="deviceType">设备类型</el-radio>
            <el-radio label="device">指定设备</el-radio>
          </el-radio-group>
          <el-select 
            v-if="scopeType === 'deviceType'"
            v-model="formData.deviceType" 
            placeholder="选择设备类型" 
            class="w-full mt-2"
          >
            <el-option label="七合一传感器" value="SENSOR_7IN1" />
            <el-option label="气象站" value="WEATHER_STATION" />
          </el-select>
          <el-input 
            v-if="scopeType === 'device'"
            v-model="formData.deviceId" 
            placeholder="输入设备ID" 
            class="mt-2"
          />
        </el-form-item>
        <el-form-item label="监测指标" required>
          <el-select v-model="formData.metric" placeholder="选择指标" class="w-full">
            <el-option label="温度 (temperature)" value="temperature" />
            <el-option label="湿度 (humidity)" value="humidity" />
            <el-option label="光照 (illuminance)" value="illuminance" />
            <el-option label="CO2 浓度 (co2)" value="co2" />
            <el-option label="土壤湿度 (soilMoisture)" value="soilMoisture" />
            <el-option label="土壤pH (soilPh)" value="soilPh" />
          </el-select>
        </el-form-item>
        <el-form-item label="阈值条件">
          <div class="flex gap-3 w-full">
            <div class="flex-1">
              <div class="text-xs text-slate-500 mb-1">最小值（低于触发）</div>
              <el-input-number v-model="formData.minValue" :precision="1" controls-position="right" class="w-full" />
            </div>
            <div class="flex-1">
              <div class="text-xs text-slate-500 mb-1">最大值（高于触发）</div>
              <el-input-number v-model="formData.maxValue" :precision="1" controls-position="right" class="w-full" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="告警级别">
          <el-select v-model="formData.level" class="w-full">
            <el-option label="低 (low)" value="low" />
            <el-option label="中 (medium)" value="medium" />
            <el-option label="高 (high)" value="high" />
            <el-option label="紧急 (critical)" value="critical" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="formData.enabled" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ editingRule ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Edit, Delete } from '@element-plus/icons-vue'
import { alarmRuleApi, type AlarmRule } from '@/api/alarmRule'
import { ElMessage, ElMessageBox } from 'element-plus'

const rules = ref<AlarmRule[]>([])
const loading = ref(false)

// Dialog
const dialogVisible = ref(false)
const editingRule = ref<AlarmRule | null>(null)
const submitting = ref(false)
const scopeType = ref<'global' | 'deviceType' | 'device'>('global')

const formData = ref({
  ruleName: '',
  deviceType: '',
  deviceId: '',
  metric: '',
  minValue: null as number | null,
  maxValue: null as number | null,
  level: 'medium',
  enabled: 1
})

const fetchRules = async () => {
  loading.value = true
  try {
    const res = await alarmRuleApi.getPage(1, 100)
    rules.value = res.records
  } catch (e) {
    console.error(e)
    ElMessage.error('获取规则列表失败')
  } finally {
    loading.value = false
  }
}

const openDialog = (rule?: AlarmRule) => {
  if (rule) {
    editingRule.value = rule
    scopeType.value = rule.deviceId ? 'device' : (rule.deviceType ? 'deviceType' : 'global')
    formData.value = {
      ruleName: rule.ruleName || '',
      deviceType: rule.deviceType || '',
      deviceId: rule.deviceId || '',
      metric: rule.metric || '',
      minValue: rule.minValue ?? null,
      maxValue: rule.maxValue ?? null,
      level: rule.level || 'medium',
      enabled: rule.enabled ?? 1
    }
  } else {
    editingRule.value = null
    scopeType.value = 'global'
    formData.value = {
      ruleName: '',
      deviceType: '',
      deviceId: '',
      metric: '',
      minValue: null,
      maxValue: null,
      level: 'medium',
      enabled: 1
    }
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formData.value.metric) {
    ElMessage.warning('请选择监测指标')
    return
  }
  
  if (formData.value.minValue === null && formData.value.maxValue === null) {
    ElMessage.warning('请设置至少一个阈值条件')
    return
  }
  
  // Prepare data based on scope
  const data: Partial<AlarmRule> = {
    ruleName: formData.value.ruleName,
    metric: formData.value.metric,
    minValue: formData.value.minValue,
    maxValue: formData.value.maxValue,
    level: formData.value.level,
    enabled: formData.value.enabled
  }
  
  if (scopeType.value === 'deviceType') {
    data.deviceType = formData.value.deviceType
    data.deviceId = undefined
  } else if (scopeType.value === 'device') {
    data.deviceId = formData.value.deviceId
    data.deviceType = undefined
  }
  
  submitting.value = true
  try {
    if (editingRule.value) {
      await alarmRuleApi.update({ id: editingRule.value.id, ...data })
      ElMessage.success('规则已更新')
    } else {
      await alarmRuleApi.save(data)
      ElMessage.success('规则已创建')
    }
    dialogVisible.value = false
    fetchRules()
  } catch (e) {
    ElMessage.error('操作失败')
    console.error(e)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (rule: AlarmRule) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除规则 "${rule.ruleName || '#' + rule.id}" 吗？`,
      '删除确认',
      { type: 'warning' }
    )
    
    await alarmRuleApi.delete(rule.id)
    ElMessage.success('规则已删除')
    fetchRules()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(e)
    }
  }
}

const handleToggle = async (rule: AlarmRule) => {
  try {
    await alarmRuleApi.update({ id: rule.id, enabled: rule.enabled })
    ElMessage.success(rule.enabled ? '规则已启用' : '规则已禁用')
  } catch (e) {
    ElMessage.error('操作失败')
    // Revert
    rule.enabled = rule.enabled === 1 ? 0 : 1
  }
}

const getMetricLabel = (metric?: string) => {
  const map: Record<string, string> = {
    temperature: '温度',
    humidity: '湿度',
    illuminance: '光照',
    co2: 'CO2',
    soilMoisture: '土壤湿度',
    soilPh: '土壤pH'
  }
  return map[metric || ''] || metric
}

const formatThreshold = (rule: AlarmRule) => {
  const parts = []
  if (rule.minValue !== null && rule.minValue !== undefined) {
    parts.push(`< ${rule.minValue}`)
  }
  if (rule.maxValue !== null && rule.maxValue !== undefined) {
    parts.push(`> ${rule.maxValue}`)
  }
  return parts.join(' 或 ') || '-'
}

const getLevelType = (level?: string) => {
  switch (level) {
    case 'critical': return 'danger'
    case 'high': return 'warning'
    case 'medium': return ''
    default: return 'info'
  }
}

onMounted(() => fetchRules())
</script>
