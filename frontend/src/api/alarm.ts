import { http } from './request'
import type { PageResult } from './device'

export interface Alarm {
  id: number
  deviceId: string
  metric: string
  value: number
  minValue: number
  maxValue: number
  level: string
  status: number // 0: Unconfirmed, 1: Resolved
  triggeredAt: string
  message: string
}

export const alarmApi = {
  getPage(page: number, size: number, status?: number) {
    return http.get<PageResult<Alarm>>('/alarm/page', { params: { page, size, status } })
  },

  updateStatus(id: number, status: number) {
    return http.put<boolean>(`/alarm/${id}/status`, null, { params: { status } })
  }
}
