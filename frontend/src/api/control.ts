import { http } from './request'
import type { PageResult } from './device'

/**
 * 控制指令
 */
export interface ControlCommand {
  id?: number
  deviceId: string
  command: string
  params?: string
  requestId?: string
  status?: number
  statusText?: string
  sentAt?: string
  ackAt?: string
  errorMsg?: string
  createdAt?: string
}

/**
 * 发送指令响应
 */
export interface SendCommandResponse {
  requestId: string
  deviceId: string
  command: string
  params?: object
  status: number
  statusText: string
  sentAt: string
  errorMsg?: string
}

export const controlApi = {
  /**
   * 分页查询指令记录
   */
  getPage(page: number, size: number, deviceId?: string) {
    return http.get<PageResult<ControlCommand>>('/control-command/page', {
      params: { page, size, deviceId }
    })
  },

  /**
   * 下发控制指令
   */
  send(data: { deviceId: string; command: string; params?: string }) {
    return http.post<SendCommandResponse>('/control-command/send', data)
  },

  /**
   * 查询指令执行状态
   */
  getStatus(requestId: string) {
    return http.get<SendCommandResponse>(`/control-command/status/${requestId}`)
  }
}
