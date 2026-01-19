import { http } from './request'

export interface Device {
  id: number
  deviceId: string
  deviceName: string
  deviceType: string
  status: number // 0: Offline, 1: Online
  location: string
  lastActiveTime: string
  capabilities?: string
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
