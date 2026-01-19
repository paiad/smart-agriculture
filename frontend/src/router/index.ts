import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/DashboardLayout.vue'),
    children: [
      {
        path: '',
        redirect: '/monitor'
      },
      {
        path: 'monitor',
        name: 'Monitor',
        component: () => import('@/views/monitor/RealTimeMonitor.vue'),
        meta: { title: '实时监控' }
      },
      {
        path: 'control',
        name: 'DeviceControl',
        component: () => import('@/views/control/DeviceControl.vue'),
        meta: { title: '设备控制' }
      },
      {
        path: 'alarm',
        name: 'AlarmCenter',
        component: () => import('@/views/alarm/AlarmCenter.vue'),
        meta: { title: '告警中心' }
      },
      {
        path: 'rule',
        name: 'AlarmRuleConfig',
        component: () => import('@/views/rule/AlarmRuleConfig.vue'),
        meta: { title: '规则配置' }
      },
      {
        path: 'device',
        name: 'DeviceList',
        component: () => import('@/views/device/DeviceList.vue'),
        meta: { title: '设备管理' }
      },
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: 'Login' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || '首页'} - 智慧农业平台`
  next()
})

export default router
