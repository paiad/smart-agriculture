<template>
  <div class="h-screen w-screen flex flex-col justify-center items-center relative overflow-hidden bg-[#FAFAF9]">
    <!-- Subtle Background Accents -->
    <div class="absolute inset-0 pointer-events-none">
      <div class="absolute top-[-10%] right-[-10%] w-[50%] h-[50%] bg-emerald-50/30 rounded-full blur-[120px]"></div>
      <div class="absolute bottom-[-10%] left-[-10%] w-[50%] h-[50%] bg-blue-50/20 rounded-full blur-[120px]"></div>
    </div>

    <div class="relative z-10 w-full max-w-[440px] px-8 animate-[fadeIn_0.8s_ease-out]">
      <div class="flex flex-col items-center mb-12">
        <div class="w-16 h-16 bg-white rounded-[20%] flex items-center justify-center mb-6 shadow-[0_4px_20px_rgba(0,0,0,0.05)] border border-gray-100">
           <img src="@/assets/logo.svg" alt="Logo" class="w-10 h-10" />
        </div>
        <h1 class="text-[#1D1D1F] text-[32px] font-bold tracking-tight mb-2">
          {{ isRegister ? '创建您的账号' : '智慧农业平台' }}
        </h1>
        <p class="text-[#86868B] text-lg font-medium">使用您的手机号进行登录</p>
      </div>

      <el-form
        :model="loginForm"
        :rules="rules"
        ref="loginFormRef"
        label-position="top"
        size="large"
        class="space-y-6"
        v-if="!isRegister"
      >
        <el-form-item prop="phone" class="!mb-0">
          <el-input
            v-model="loginForm.phone"
            placeholder="手机号"
            class="apple-input"
          />
        </el-form-item>

        <el-form-item prop="password" class="!mb-0">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            show-password
            class="apple-input"
          />
        </el-form-item>

        <div class="flex justify-between items-center px-1">
          <el-checkbox v-model="rememberMe" class="apple-checkbox">记住我</el-checkbox>
          <a href="#" class="text-sm text-[#0066CC] hover:underline">忘记密码？</a>
        </div>

        <el-form-item class="!mb-0">
          <el-button
            type="primary"
            class="!w-full !h-[50px] !text-[17px] !font-medium !rounded-[12px] !bg-[#1D1D1F] !border-none hover:!bg-[#000000] active:!scale-[0.98] transition-all duration-200"
            @click="handleLogin"
            :loading="loading"
          >
            登 录
          </el-button>
        </el-form-item>
        
        <div class="text-center mt-8">
           <p class="text-[#86868B] text-sm">
             还没有账号？ 
             <a href="#" @click.prevent="toggleMode" class="text-[#0066CC] hover:underline ml-1 font-medium">立即注册</a>
           </p>
        </div>
      </el-form>

      <!-- 注册表单 -->
      <el-form
        v-else
        :model="registerForm"
        :rules="registerRules"
        ref="loginFormRef"
        label-position="top"
        size="large"
        class="space-y-6"
      >
        <el-form-item prop="phone" class="!mb-0">
          <el-input
            v-model="registerForm.phone"
            placeholder="手机号"
            class="apple-input"
          />
        </el-form-item>

        <el-form-item prop="password" class="!mb-0">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="设置密码"
            show-password
            class="apple-input"
          />
        </el-form-item>

        <el-form-item prop="confirmPassword" class="!mb-8">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            show-password
            class="apple-input"
          />
        </el-form-item>

        <el-form-item class="!mb-0">
          <el-button
            type="primary"
            class="!w-full !h-[50px] !text-[17px] !font-medium !rounded-[12px] !bg-[#1D1D1F] !border-none hover:!bg-[#000000] active:!scale-[0.98] transition-all duration-200"
            @click="handleRegister"
            :loading="loading"
          >
            注 册
          </el-button>
        </el-form-item>

        <div class="text-center mt-8">
           <p class="text-[#86868B] text-sm">
             已有账号？ 
             <a href="#" @click.prevent="toggleMode" class="text-[#0066CC] hover:underline ml-1 font-medium">返回登录</a>
           </p>
        </div>
      </el-form>
    </div>

    <!-- Footer -->
    <div class="absolute bottom-12 w-full text-center px-4">
      <div class="flex justify-center gap-6 mb-4">
        <a href="#" class="text-[#86868B] text-xs hover:text-[#1D1D1F]">隐私政策</a>
        <a href="#" class="text-[#86868B] text-xs hover:text-[#1D1D1F]">服务条款</a>
        <a href="#" class="text-[#86868B] text-xs hover:text-[#1D1D1F]">帮助中心</a>
      </div>
      <p class="text-[#86868B] text-[11px]">&copy; 2025 Smart Agriculture Inc. 保留所有权利。</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { userApi } from '@/api'

const router = useRouter()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const rememberMe = ref(false)
const isRegister = ref(false)

const loginForm = reactive({
  phone: '',
  password: ''
})

const registerForm = reactive({
  phone: '',
  password: '',
  confirmPassword: ''
})

const rules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const registerRules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

onMounted(() => {
  const saved = localStorage.getItem('savedCredentials')
  if (saved) {
    try {
      const { phone, password } = JSON.parse(saved)
      loginForm.phone = phone
      loginForm.password = password
      rememberMe.value = true
    } catch (e) {
      console.error('Failed to parse saved credentials', e)
    }
  }
})

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const token = await userApi.login(loginForm)
        localStorage.setItem('token', token)
        if (rememberMe.value) {
          localStorage.setItem('savedCredentials', JSON.stringify(loginForm))
        } else {
          localStorage.removeItem('savedCredentials')
        }
        ElMessage.success('登录成功')
        router.push('/')
      } catch {
        // Handle error
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegister = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userApi.register(registerForm)
        ElMessage.success('注册成功')
        isRegister.value = false
        loginForm.phone = registerForm.phone
        loginForm.password = ''
      } catch {
        // Handle error
      } finally {
        loading.value = false
      }
    }
  })
}

const toggleMode = () => {
  isRegister.value = !isRegister.value
  loginFormRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

:deep(.apple-input) {
  .el-input__wrapper {
    background-color: #ffffff;
    box-shadow: 0 0 0 1px #D2D2D7 inset;
    border-radius: 12px;
    padding: 0 16px;
    height: 50px;
    transition: all 0.2s ease;
    
    &:hover {
      box-shadow: 0 0 0 1px #86868B inset;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 2px #0066CC inset !important;
    }
  }
  
  .el-input__inner {
    color: #1D1D1F;
    font-family: Inter, -apple-system, BlinkMacSystemFont, sans-serif;
    font-size: 17px;
    
    &::placeholder {
      color: #86868B;
    }
  }
}

.apple-checkbox {
  :deep(.el-checkbox__label) {
    color: #1D1D1F;
    font-size: 14px;
    font-weight: 500;
  }
  
  :deep(.el-checkbox__inner) {
    border-radius: 4px;
    border-color: #D2D2D7;
    
    &:after {
      border-color: #ffffff;
    }
  }
  
  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: #0066CC;
    border-color: #0066CC;
  }
}

/* Autofill Hack */
:deep(.el-input__inner:-webkit-autofill),
:deep(.el-input__inner:-webkit-autofill:hover),
:deep(.el-input__inner:-webkit-autofill:focus),
:deep(.el-input__inner:-webkit-autofill:active) {
  -webkit-text-fill-color: #1D1D1F !important;
  transition: background-color 5000s ease-in-out 0s;
}

/* Fix form validation - reserve space for error message */
:deep(.el-form-item) {
  margin-bottom: 0 !important;
  min-height: 65px; /* 50px input + 12px for error message */
}

:deep(.el-form-item__content) {
  flex-wrap: wrap;
}

:deep(.el-form-item__error) {
  position: relative;
  padding-top: 4px;
  font-size: 12px;
  line-height: 1;
}
</style>

