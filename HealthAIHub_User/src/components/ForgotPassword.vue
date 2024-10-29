<!-- src/components/ForgotPassword.vue -->
<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage, ElIcon } from 'element-plus'
import type { FormRules, FormInstance } from 'element-plus'
import { emailRules, passwordRules, confirmPasswordRules, verificationCodeRules } from '@/utils/validationRules'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const form = reactive({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmPassword: '',
})

const rules: FormRules = {
  email: emailRules,
  verificationCode: verificationCodeRules,
  newPassword: passwordRules,
  confirmPassword: confirmPasswordRules(() => form.newPassword),
}

const isSendingCode = ref(false)

const handleSendCode = async () => {
  if (!form.email) {
    ElMessage.warning('请先输入注册邮箱')
    return
  }
  try {
    isSendingCode.value = true

    // 检查邮箱是否已注册
    const checkResult = await authStore.checkUsernameEmailAvailability(undefined, form.email)
    if (checkResult.data) {
      // 如果 data 为 true，说明邮箱未注册
      ElMessage.error('该邮箱尚未注册，无法重置密码')
      isSendingCode.value = false
      return
    }

    // 邮箱已注册，继续发送验证码
    ElMessage.info('正在发送邮箱验证码，请稍候...')
    const response = await authStore.sendVerificationCodeToEmail(form.email)
    if (response && response.code === 200) {
      ElMessage.success('邮箱验证码已发送，请查收邮箱')
    } else {
      ElMessage.error(response?.message || '发送邮箱验证码失败')
    }
  } catch (error) {
    console.error('发送邮箱验证码失败:', error)
    ElMessage.error('发送邮箱验证码失败，请稍后重试')
  } finally {
    isSendingCode.value = false
  }
}

const handleResetPassword = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        // 验证邮箱验证码
        const verifyResult = await authStore.verifyEmailVerificationCode(form.email, form.verificationCode)
        if (verifyResult.code !== 200 || !verifyResult.data) {
          ElMessage.error(verifyResult.message || '邮箱验证码验证失败，请检查后重试')
          return
        }

        // 重置密码
        const resetResult = await authStore.resetUserPassword(form.email, form.newPassword, form.verificationCode)
        if (resetResult.code === 200) {
          ElMessage.success('密码重置成功，请使用新密码登录')
          router.push('/')
        } else {
          ElMessage.error(resetResult.message || '密码重置失败，请稍后重试')
        }
      } catch (error) {
        console.error('密码重置失败:', error)
        ElMessage.error('密码重置失败，请稍后重试')
      }
    } else {
      console.log('表单验证失败', fields)
      ElMessage.error('表单验证失败，请检查输入')
    }
  })
}

const goBack = () => {
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="forgot-password">
    <el-button class="back-button" @click="goBack">
      <el-icon>
        <ArrowLeft />
      </el-icon>返回登录
    </el-button>
    <h2>重置密码</h2>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="forgot-password-form"
      hide-required-asterisk>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入注册邮箱" />
      </el-form-item>
      <el-form-item label="邮箱验证码" prop="verificationCode">
        <el-input v-model="form.verificationCode" placeholder="请输入邮箱验证码" class="verification-code-input" />
        <el-button @click="handleSendCode" :disabled="!authStore.canSendCode || isSendingCode" :loading="isSendingCode"
          class="send-code-button">
          {{ isSendingCode ? '发送中...' : (authStore.isCodeSent ? `重新发送(${authStore.countdown}s)` : '发送验证码') }}
        </el-button>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" show-password />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleResetPassword">重置密码</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.forgot-password {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
}

.back-button {
  background: none;
  border: none;
  color: #409EFF;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.back-button:hover {
  background: none;
  color: #79bbff;
}

.back-button .el-icon {
  margin-right: 5px;
}

.forgot-password-form {
  margin-top: 20px;
}

.verification-code-input {
  width: calc(100% - 120px);
  margin-right: 10px;
}

.send-code-button {
  width: 110px;
}
</style>
