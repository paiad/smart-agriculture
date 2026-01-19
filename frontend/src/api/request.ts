import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// API 响应类型
export interface ApiResult<T = any> {
  code: number
  msg: string
  data: T
}

const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      config.headers.token = token // Add custom token header
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResult>) => {
    const { code, msg, data } = response.data
    
    // Standard ApiResult success
    if (code === 200) {
      return data as any
    }

    // Support raw response (no wrapper)
    // If code is undefined and http status is 200, assume success and return full data
    if (code === undefined && response.status === 200) {
      return response.data as any
    }
    
    // Business error or failure
    ElMessage.error(msg || '请求失败')
    return Promise.reject(new Error(msg))
  },
  (error) => {
    // 网络错误
    const message = error.response?.data?.msg || error.message || '网络错误'
    ElMessage.error(message)
    
    // 401 跳转登录
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    
    return Promise.reject(error)
  }
)

// 封装请求方法
export const http = {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.get(url, config)
  },
  post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return request.post(url, data, config)
  },
  put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return request.put(url, data, config)
  },
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.delete(url, config)
  }
}

export default request
