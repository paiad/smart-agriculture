<template>
  <div class="h-screen w-screen flex bg-slate-50">
    <!-- Sidebar -->
    <aside class="w-[180px] bg-white border-r border-slate-200 flex flex-col">
      <!-- Logo -->
      <div class="h-16 flex items-center px-5 border-b border-slate-100">
        <div
          class="w-9 h-9 bg-white rounded-lg flex items-center justify-center mr-3 border border-slate-100 shadow-sm"
        >
          <img src="@/assets/logo.svg" alt="Logo" class="w-6 h-6" />
        </div>
        <span class="text-lg font-semibold text-slate-800">智慧农业</span>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 px-3 py-4 space-y-1">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          custom
          v-slot="{ isActive, isExactActive, navigate }"
        >
          <div
            @click="navigate"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg cursor-pointer transition-all duration-200 group"
            :class="[
              (item.path === '/' ? isExactActive : isActive)
                ? 'bg-slate-900 text-white shadow-md shadow-slate-200'
                : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900',
            ]"
          >
            <div :class="item.icon" class="w-5 h-5 transition-transform duration-200 group-hover:scale-110"></div>
            <span class="text-sm font-medium">{{ item.title }}</span>
          </div>
        </router-link>
      </nav>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col min-w-0 bg-[#F8FAFC]">
      <!-- Header -->
      <header
        class="h-16 bg-white border-b border-slate-100 flex items-center justify-between px-6 shrink-0 sticky top-0 z-20"
      >
        <div class="flex items-center gap-2 text-sm">
           <div class="text-slate-400">/</div>
          <span class="text-slate-800 font-semibold tracking-tight">{{ route.meta.title || 'Dashboard' }}</span>
        </div>
        <div class="flex items-center gap-4">
          <!-- User Profile -->
          <div
            class="flex items-center gap-3 pl-2 pr-1 py-1 rounded-full border border-slate-100 bg-slate-50 hover:bg-white hover:shadow-sm cursor-pointer transition-all duration-200"
          >
            <el-avatar
              :size="32"
              :src="userInfo?.avatar"
              class="!bg-indigo-600 text-white text-xs font-bold border-2 border-white"
            >
              {{
                (
                  userInfo?.username?.[0] ||
                  userInfo?.phone?.[0] ||
                  "U"
                ).toUpperCase()
              }}
            </el-avatar>
            <div class="flex flex-col items-start min-w-0 pr-2">
              <span
                class="text-xs font-bold text-slate-700 truncate leading-tight"
              >
                {{
                  userInfo?.nickname ||
                  userInfo?.username ||
                  userInfo?.phone ||
                  "User"
                }}
              </span>
              <span class="text-[10px] text-slate-400 font-medium leading-tight">
                {{ userInfo?.identity || "Admin" }}
              </span>
            </div>
          </div>

          <div class="h-6 w-[1px] bg-slate-200/60 mx-1"></div>

          <div class="flex items-center gap-2">
            <div
              class="w-8 h-8 rounded-lg flex items-center justify-center hover:bg-slate-100 cursor-pointer transition-colors"
              title="通知"
            >
              <el-icon :size="18" class="text-slate-500"><Bell /></el-icon>
            </div>
            <div
              class="w-8 h-8 rounded-lg flex items-center justify-center hover:bg-red-50 hover:text-red-500 cursor-pointer transition-colors"
              title="退出登录"
              @click="handleLogout"
            >
              <el-icon
                :size="18"
                class="text-slate-500 hover:text-red-500 transition-colors"
                ><SwitchButton
              /></el-icon>
            </div>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="flex-1 overflow-auto p-6 md:p-8">
        <RouterView v-slot="{ Component }">
           <transition name="fade-slight" mode="out-in">
             <component :is="Component" />
           </transition>
        </RouterView>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Bell, SwitchButton } from "@element-plus/icons-vue";
import { userApi, type UserInfo } from "@/api/user";

const route = useRoute();
const router = useRouter();
const userInfo = ref<UserInfo | null>(null);

const menuItems = [
  {
    path: "/monitor",
    title: "实时监控",
    icon: "i-solar-monitor-camera-linear",
  },
  { path: "/device", title: "设备管理", icon: "i-solar-server-square-linear" },
  { path: "/alarm", title: "告警中心", icon: "i-solar-bell-bing-linear" },
  { path: "/rule", title: "规则配置", icon: "i-solar-settings-linear" },
  { path: "/control", title: "设备控制", icon: "i-solar-gamepad-linear" },
];

onMounted(async () => {
  try {
    const res = await userApi.getCurrentUser();
    userInfo.value = res;
  } catch (error) {
    console.error("Failed to fetch user info:", error);
  }
});

const handleLogout = () => {
  localStorage.removeItem("token");
  router.push("/login");
};
</script>

<style scoped>
:deep(.el-avatar) {
  --el-avatar-bg-color: #dbeafe;
  color: #2563eb;
}
</style>
