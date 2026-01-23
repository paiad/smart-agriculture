import { http } from './request'
import type { PageResult } from './device'

export interface AlarmRule {
  id: number
  ruleName?: string
  deviceId?: string
  deviceType?: string
  metric: string
  condition?: string
  minValue?: number | null
  maxValue?: number | null
  level: string
  enabled: number
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
