<template>
  <div class="h-screen w-screen flex bg-slate-50">
    <!-- Sidebar -->
    <aside class="w-[240px] bg-white border-r border-slate-200 flex flex-col">
      <!-- Logo -->
      <div class="h-16 flex items-center px-5 border-b border-slate-100">
        <div class="w-9 h-9 bg-white rounded-lg flex items-center justify-center mr-3 border border-slate-100 shadow-sm">
          <img src="@/assets/logo.svg" alt="Logo" class="w-6 h-6" />
        </div>
        <span class="text-lg font-semibold text-slate-800">智慧农业</span>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 p-3 space-y-1">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          custom
          v-slot="{ isActive, isExactActive, navigate }"
        >
          <div
            @click="navigate"
            class="sidebar-item"
            :class="{ 'sidebar-item-active': item.path === '/' ? isExactActive : isActive }"
          >
            <div :class="item.icon" class="w-5 h-5"></div>
            <span class="text-sm">{{ item.title }}</span>
          </div>
        </router-link>
      </nav>


    </aside>

      <!-- Main Content -->
    <div class="flex-1 flex flex-col min-w-0">
      <!-- Header -->
      <header class="h-14 bg-white border-b border-slate-200 flex items-center justify-between px-6">
        <div class="flex items-center gap-2 text-sm">
          <span class="text-slate-400">Home</span>
          <span v-if="route.meta.title !== 'Home'" class="text-slate-300">/</span>
          <span v-if="route.meta.title !== 'Home'" class="text-slate-700 font-medium">{{ route.meta.title }}</span>
        </div>
        <div class="flex items-center gap-4">
          <!-- User Profile -->
          <div class="flex items-center gap-3 hover:bg-slate-50 py-1 px-2 rounded-lg cursor-pointer transition-colors">
            <el-avatar :size="32" :src="userInfo?.avatar" class="bg-brand-100 text-brand-600 text-sm">
              {{ (userInfo?.username?.[0] || userInfo?.phone?.[0] || 'U').toUpperCase() }}
            </el-avatar>
            <div class="flex flex-col items-start min-w-0">
              <span class="text-sm font-medium text-slate-800 truncate leading-tight">
                {{ userInfo?.nickname || userInfo?.username || userInfo?.phone || '加载中...' }}
              </span>
              <span class="text-[10px] text-slate-400 leading-tight">
                {{ userInfo?.identity || '普通用户' }}
              </span>
            </div>
          </div>
          
          <div class="h-4 w-[1px] bg-slate-200 mx-1"></div>

          <div class="flex items-center gap-2">
            <div class="w-8 h-8 rounded-lg flex items-center justify-center hover:bg-slate-100 cursor-pointer transition-colors" title="通知">
              <el-icon :size="18" class="text-slate-500"><Bell /></el-icon>
            </div>
            <div 
              class="w-8 h-8 rounded-lg flex items-center justify-center hover:bg-red-50 hover:text-red-500 cursor-pointer transition-colors"
              title="退出登录"
              @click="handleLogout"
            >
              <el-icon :size="18" class="text-slate-500 hover:text-red-500 transition-colors"><SwitchButton /></el-icon>
            </div>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="flex-1 overflow-auto p-6">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Bell, Setting, SwitchButton } from '@element-plus/icons-vue'
import { userApi, type UserInfo } from '@/api/user'

const route = useRoute()
const router = useRouter()
const userInfo = ref<UserInfo | null>(null)

const menuItems = [
  { path: '/', title: 'Home', icon: 'i-solar-home-smile-linear' },
  { path: '/about', title: 'About', icon: 'i-solar-info-circle-linear' },
]

onMounted(async () => {
  try {
    const res = await userApi.getCurrentUser()
    userInfo.value = res
  } catch (error) {
    console.error('Failed to fetch user info:', error)
  }
})

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
:deep(.el-avatar) {
  --el-avatar-bg-color: #DBEAFE;
  color: #2563EB;
}
</style>
