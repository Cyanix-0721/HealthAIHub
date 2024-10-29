<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage, ElRadioGroup, ElRadio } from 'element-plus'
import { emailRules, loginPasswordRules, verificationCodeRules, loginUsernameRules } from '@/utils/validationRules'
import { useRouter } from 'vue-router'
import type { UserLoginDto } from '@/service/api'
import { useAuthStore } from '@/stores/auth'
import { LoginType } from '@/service/api'

const router = useRouter()
const authStore = useAuthStore()

const loginForm = reactive<UserLoginDto>({
  username: '',
  email: '',
  password: '',
  emailVerificationCode: '',
  loginType: LoginType.USERNAME
})

const rules = computed(() => ({
  username: loginForm.loginType === LoginType.USERNAME ? loginUsernameRules : [],
  email: [LoginType.EMAIL, LoginType.EMAIL_VERIFICATION_CODE].includes(loginForm.loginType) ? emailRules : [],
  password: [LoginType.USERNAME, LoginType.EMAIL].includes(loginForm.loginType) ? loginPasswordRules : [],
  emailVerificationCode: loginForm.loginType === LoginType.EMAIL_VERIFICATION_CODE ? verificationCodeRules : []
}))

const formRef = ref<InstanceType<typeof ElForm> | null>(null)

const isSendingCode = ref(false)

const handleSendCode = async () => {
  if (!loginForm.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  try {
    isSendingCode.value = true

    // 先检查邮箱是否已注册
    const checkResult = await authStore.checkUsernameEmailAvailability(undefined, loginForm.email)
    if (checkResult.data) {
      ElMessage.error('该邮箱尚未注册，请先注册')
      return
    }

    // 邮箱已注册，开始发送验证码
    ElMessage.info('正在发送验证码，请稍候...')
    const response = await authStore.sendVerificationCodeToEmail(loginForm.email)
    if (response && response.code === 200) {
      ElMessage.success('验证码已发送，请查收邮箱')
      // 不需要手动调用 startCountdown，因为它现在在 auth store 中自动处理
    } else {
      ElMessage.error(response?.message || '发送验证码失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error('发送验证码失败，请稍后重试')
  } finally {
    isSendingCode.value = false
  }
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        const loginData: UserLoginDto = {
          loginType: loginForm.loginType,
          username: loginForm.loginType === LoginType.USERNAME ? loginForm.username : undefined,
          email: [LoginType.EMAIL, LoginType.EMAIL_VERIFICATION_CODE].includes(loginForm.loginType) ? loginForm.email : undefined,
          password: [LoginType.USERNAME, LoginType.EMAIL].includes(loginForm.loginType) ? loginForm.password : undefined,
          emailVerificationCode: loginForm.loginType === LoginType.EMAIL_VERIFICATION_CODE ? loginForm.emailVerificationCode : undefined
        }

        const loginSuccess = await authStore.loginUser(loginData)
        if (loginSuccess) {
          ElMessage.success('登录成功')
          console.log('开始跳转到 /app/assistant')
          try {
            await router.push('/app/assistant')
            console.log('路由跳转完成')
          } catch (error) {
            console.error('路由跳转失败:', error)
          }
        } else {
          ElMessage.error('登录失败，请稍后重试')
        }
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error('登录失败，请稍后重试')
      }
    } else {
      console.log('验证失败', fields)
      ElMessage.error('表单验证失败，请检查输入')
    }
  })
}

const goToForgotPassword = () => {
  router.push('/forgot-password')
}
</script>

<!-- TODO: 人机验证码功能 -->
<template>
  <el-form ref="formRef" :model="loginForm" :rules="rules" label-width="120px" class="login-form"
    hide-required-asterisk>
    <el-form-item label="登录方式">
      <el-radio-group v-model="loginForm.loginType">
        <el-radio :value="LoginType.USERNAME">用户名密码</el-radio>
        <el-radio :value="LoginType.EMAIL">邮箱密码</el-radio>
        <el-radio :value="LoginType.EMAIL_VERIFICATION_CODE">邮箱验证码</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item v-if="loginForm.loginType === LoginType.USERNAME" label="用户名" prop="username">
      <el-input v-model="loginForm.username" placeholder="请输入用户名" />
    </el-form-item>
    <el-form-item v-if="loginForm.loginType !== LoginType.USERNAME" label="邮箱" prop="email">
      <el-input v-model="loginForm.email" placeholder="请输入邮箱地址" />
    </el-form-item>
    <el-form-item v-if="loginForm.loginType !== LoginType.EMAIL_VERIFICATION_CODE" label="密码" prop="password">
      <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
    </el-form-item>
    <el-form-item v-if="loginForm.loginType === LoginType.EMAIL_VERIFICATION_CODE" label="邮箱验证码"
      prop="emailVerificationCode">
      <el-input v-model="loginForm.emailVerificationCode" placeholder="请输入邮箱验证码" class="verification-code-input" />
      <el-button @click="handleSendCode" :disabled="!authStore.canSendCode || isSendingCode" class="send-code-button">
        {{ authStore.isCodeSent ? `重新发送(${authStore.countdown}s)` : '发送验证码' }}
      </el-button>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleLogin">登录</el-button>
      <el-button @click="goToForgotPassword">忘记密码</el-button>
    </el-form-item>
  </el-form>
</template>

<style scoped>
.login-form {
  max-width: 500px;
  margin: 0 auto;
}

.verification-code-input {
  width: calc(100% - 120px);
  margin-right: 10px;
}

.send-code-button {
  width: 110px;
}
</style>
