<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus'
import type { FormRules, FormItemRule } from 'element-plus'
import { usernameRules, passwordRules, confirmPasswordRules, emailRules, verificationCodeRules } from '@/utils/validationRules'
import { useRouter } from 'vue-router'
import type { AdminRegisterDto } from '@/service/api'
import { useAuthStore } from '@/stores/auth'
import { LoginType } from '@/service/api'

const router = useRouter()
const authStore = useAuthStore()

const registerForm = reactive<AdminRegisterDto & { confirmPassword: string }>({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  emailVerificationCode: ''
})

const rules: FormRules = {
  username: usernameRules as FormItemRule[],
  email: emailRules as FormItemRule[],
  password: passwordRules as FormItemRule[],
  confirmPassword: confirmPasswordRules(() => registerForm.password) as FormItemRule[],
  emailVerificationCode: verificationCodeRules as FormItemRule[]
}

const formRef = ref<InstanceType<typeof ElForm> | null>(null)

const handleSendCode = async () => {
  if (!registerForm.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  try {
    // 检查邮箱是否可用
    const checkResult = await authStore.checkUsernameEmailAvailability(undefined, registerForm.email)
    if (checkResult.data) {
      // 邮箱可用（用户不存在），发送验证码
      const response = await authStore.sendVerificationCodeToEmail(registerForm.email)
      if (response && response.code === 200) {
        ElMessage.success('验证码已发送，请查收邮箱')
        // 不需要手动调用 startCountdown，因为它现在在 auth store 中自动处理
      } else {
        ElMessage.error(response?.message || '发送验证码失败')
      }
    } else {
      ElMessage.error(checkResult.message)
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error('发送验证码失败，请稍后重试')
  }
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        // 检查用户名和邮箱是否可用
        const checkUsernameResult = await authStore.checkUsernameEmailAvailability(registerForm.username, undefined)
        const checkEmailResult = await authStore.checkUsernameEmailAvailability(undefined, registerForm.email)

        if (!checkUsernameResult.data) {
          ElMessage.error('用户名已被占用')
          return
        }

        if (!checkEmailResult.data) {
          ElMessage.error('邮箱已被注册')
          return
        }

        // 创建一个新对象，不包含 confirmPassword
        const registerData: AdminRegisterDto = {
          username: registerForm.username,
          email: registerForm.email,
          password: registerForm.password,
          emailVerificationCode: registerForm.emailVerificationCode
        }

        // 继续注册流程
        const response = await authStore.registerAdmin(registerData)
        if (response.code === 200) {
          console.log('注册成功:', response.data)
          ElMessage.success('注册成功，正在为您自动登录')

          // 使用注册时的用户名和密码进行登录
          const loginSuccess = await authStore.loginAdmin({
            username: registerForm.username,
            password: registerForm.password,
            loginType: LoginType.USERNAME
          })

          if (loginSuccess) {
            router.push('/app/user-management')
          } else {
            ElMessage.error('自动登录失败，请手动登录')
            router.push('/')
          }
        } else {
          ElMessage.error(response.message || '注册失败，请稍后重试')
        }
      } catch (error) {
        console.error('注册失败:', error)
        ElMessage.error('注册失败，请稍后重试')
      }
    } else {
      console.log('验证失败', fields)
      ElMessage.error('表单验证失败，请检查输入')
    }
  })
}
</script>

<!-- TODO: 人机验证码功能 -->
<template>
  <el-form ref="formRef" :model="registerForm" :rules="rules" label-width="120px" class="register-form"
    hide-required-asterisk>
    <el-form-item label="用户名" prop="username">
      <el-input v-model="registerForm.username" placeholder="请输入用户名" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="registerForm.email" placeholder="请输入邮箱地址" />
    </el-form-item>
    <el-form-item label="验证码" prop="emailVerificationCode">
      <el-input v-model="registerForm.emailVerificationCode" placeholder="请输入验证码" class="verification-code-input" />
      <el-button @click="handleSendCode" :disabled="!authStore.canSendCode" class="send-code-button">
        {{ authStore.isCodeSent ? `重新发送(${authStore.countdown}s)` : '发送验证码' }}
      </el-button>
    </el-form-item>
    <el-form-item label="密码" prop="password">
      <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleRegister">注册</el-button>
    </el-form-item>
  </el-form>
</template>

<style scoped>
.register-form {
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
