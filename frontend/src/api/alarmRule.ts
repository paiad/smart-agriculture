import { http } from './request'
import type { PageResult } from './device'

export interface AlarmRule {
  id: number
  deviceId?: string
  deviceType?: string
  metric: string
  condition?: string // Helper field if needed for UI
  minValue?: number
  maxValue?: number
  level: string
  enabled: number // 0: Disabled, 1: Enabled
  name?: string // Not in DB but useful for UI description? DB doesn't have name, maybe remove.
  // Actually checking DB schema: id, device_id, device_type, metric, min_value, max_value, level, enabled 
}

export const alarmRuleApi = {
  getPage(page: number, size: number) {
    return http.get<PageResult<AlarmRule>>('/alarm-rule/page', { params: { page, size } })
  },
  
  save(data: Partial<AlarmRule>) {
    return http.post<boolean>('/alarm-rule', data)
  },

  update(data: Partial<AlarmRule>) {
    return http.put<boolean>('/alarm-rule', data)
  },

  delete(id: number) {
    return http.delete<boolean>(`/alarm-rule/${id}`)
  }
}
