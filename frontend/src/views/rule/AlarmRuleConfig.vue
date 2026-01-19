<template>
  <div class="bg-white rounded-xl border border-slate-100 shadow-sm p-5">
    <div class="flex items-center justify-between mb-6">
        <h2 class="text-lg font-semibold text-slate-800">告警规则配置</h2>
        <el-button type="primary" icon="Plus">新增规则</el-button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        <div v-for="(rule, i) in rules" :key="i" class="border border-slate-200 rounded-lg p-4 hover:border-brand-500 transition-colors cursor-pointer relative group">
            <div class="absolute top-4 right-4 opacity-0 group-hover:opacity-100 transition-opacity">
                <el-button circle size="small" icon="Edit" />
            </div>
            
            <div class="flex items-center gap-2 mb-3">
                <span class="font-semibold text-slate-700">规则 #{{ rule.id }}</span>
                <el-tag size="small" :type="rule.enabled ? 'success' : 'info'" effect="plain">{{ rule.enabled ? '启用' : '禁用' }}</el-tag>
            </div>
            
            <div class="space-y-2 text-sm">
                <div class="flex justify-between">
                    <span class="text-slate-500">适用设备:</span>
                    <span class="font-medium text-slate-700">{{ rule.deviceType || '通用' }}</span>
                </div>
                <div class="flex justify-between">
                    <span class="text-slate-500">监测指标:</span>
                    <span class="font-medium text-slate-700">{{ rule.metric }}</span>
                </div>
                <div class="flex justify-between">
                    <span class="text-slate-500">阈值条件:</span>
                    <span class="font-medium text-red-500">
                        {{ rule.minValue !== null ? '> ' + rule.minValue : '' }} 
                        {{ rule.maxValue !== null ? '< ' + rule.maxValue : '' }}
                    </span>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { alarmRuleApi, type AlarmRule } from '@/api/alarmRule'

const rules = ref<AlarmRule[]>([])

const fetchRules = async () => {
    try {
        const res = await alarmRuleApi.getPage(1, 100)
        rules.value = res.records
    } catch (e) {
        console.error(e)
    }
}

onMounted(() => fetchRules())
</script>
