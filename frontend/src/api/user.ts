import { http } from './request'

export interface LoginParams {
  phone: string
  password: string
}

export interface UserInfo {
  id: number
  username: string
  phone: string
  nickname: string
  avatar: string
  identity: string
  openid?: string
  createTime: string
}

export const userApi = {
  /** 用户登录 */
  login(params: LoginParams) {
    return http.post<string>('/user/login', params)
  },

  /** 获取当前用户信息 */
  getCurrentUser() {
    return http.get<UserInfo>('/user/info')
  },

  /** 用户注册 */
  register(params: LoginParams) {
    return http.post<void>('/user/register', params)
  }
}
