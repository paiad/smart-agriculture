<template>
  <div class="h-screen w-screen flex bg-slate-50">
    <!-- Sidebar -->
    <aside class="w-[240px] bg-white border-r border-slate-200 flex flex-col">
      <!-- Logo -->
      <div class="h-16 flex items-center px-5 border-b border-slate-100">
        <div class="w-9 h-9 bg-brand-500 rounded-lg flex items-center justify-center mr-3">
          <el-icon :size="20" class="text-white"><Sunny /></el-icon>
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
          v-slot="{ isActive, navigate }"
        >
          <div
            @click="navigate"
            class="sidebar-item"
            :class="{ 'sidebar-item-active': isActive }"
          >
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
            <span class="text-sm">{{ item.title }}</span>
          </div>
        </router-link>
      </nav>

      <!-- User Section -->
      <div class="p-3 border-t border-slate-100">
        <div class="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-slate-50 cursor-pointer transition-colors">
          <el-avatar :size="32" class="bg-brand-100 text-brand-600 text-sm">管</el-avatar>
          <div class="flex-1 min-w-0">
            <div class="text-sm font-medium text-slate-800 truncate">管理员</div>
            <div class="text-xs text-slate-400">在线</div>
          </div>
          <el-icon 
            :size="16" 
            class="text-slate-400 hover:text-slate-600 transition-colors"
            @click.stop="handleLogout"
          >
            <SwitchButton />
          </el-icon>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col min-w-0">
      <!-- Header -->
      <header class="h-14 bg-white border-b border-slate-200 flex items-center justify-between px-6">
        <div class="flex items-center gap-2 text-sm">
          <span class="text-slate-400">首页</span>
          <span v-if="route.meta.title !== '首页'" class="text-slate-300">/</span>
          <span v-if="route.meta.title !== '首页'" class="text-slate-700 font-medium">{{ route.meta.title }}</span>
        </div>
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 rounded-lg flex items-center justify-center hover:bg-slate-100 cursor-pointer transition-colors">
            <el-icon :size="18" class="text-slate-500"><Bell /></el-icon>
          </div>
          <div class="w-8 h-8 rounded-lg flex items-center justify-center hover:bg-slate-100 cursor-pointer transition-colors">
            <el-icon :size="18" class="text-slate-500"><Setting /></el-icon>
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
import { useRoute, useRouter } from 'vue-router'
import { Sunny, HomeFilled, InfoFilled, Bell, Setting, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const menuItems = [
  { path: '/', title: '首页', icon: HomeFilled },
  { path: '/about', title: '关于', icon: InfoFilled },
]

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
