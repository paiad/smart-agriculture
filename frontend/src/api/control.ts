import { http } from './request'
import type { PageResult } from './device'

export interface ControlCommand {
  id: number
  deviceId: string
  command: string
  params: string // JSON string
  status: number
  requestId: string
  errorMsg: string
  createdAt: string
  sentAt: string
  ackAt: string
}

export const controlApi = {
  getPage(page: number, size: number, deviceId?: string) {
    return http.get<PageResult<ControlCommand>>('/control-command/page', { params: { page, size, deviceId } })
  },

  sendCommand(data: { deviceId: string, command: string, params: string }) {
    return http.post<boolean>('/control-command/send', data)
  }
}
