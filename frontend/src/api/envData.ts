import { http } from './request'
import type { PageResult } from './device'

export interface EnvData {
  id: number
  deviceId: string
  ts: string
  soilHumidity: number
  soilEc: number
  soilPh: number
  co2: number
  nitrogen: number
  phosphorus: number
  soilTemp: number
  potassium: number
  windDir: string
  illuminance: number
  humidity: number
  windSpeed: number
  precip: number
  temperature: number
  pressure: number
  rainfall: number
}

export const envDataApi = {
  getPage(page: number, size: number, deviceId?: string) {
    return http.get<PageResult<EnvData>>('/env-data/page', { params: { page, size, deviceId } })
  },

  getLatest(deviceId: string) {
    return http.get<EnvData>('/env-data/latest', { params: { deviceId } })
  }
}
