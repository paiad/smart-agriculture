import { http } from './request'

/**
 * 设备信息 (与后端 Device POJO 对应)
 */
export interface Device {
  id: number
  deviceId: string
  deviceType?: string
  name?: string
  greenhouseId?: number
  locationDesc?: string
  status?: number        // 0停用 1启用 2故障 3维护
  online?: number        // 0离线 1在线
  lastSeenAt?: string
  lastDataAt?: string
  vendor?: string
  model?: string
  fwVersion?: string
  protocol?: string
  capabilities?: string
  remark?: string
  createdAt?: string
  updatedAt?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export const deviceApi = {
  getPage(page: number, size: number) {
    return http.get<PageResult<Device>>('/device/page', { params: { page, size } })
  },
  
  save(data: Partial<Device>) {
    return http.post<boolean>('/device', data)
  },

  update(data: Partial<Device>) {
    return http.put<boolean>('/device', data)
  },

  delete(id: number) {
    return http.delete<boolean>(`/device/${id}`)
  }
}
