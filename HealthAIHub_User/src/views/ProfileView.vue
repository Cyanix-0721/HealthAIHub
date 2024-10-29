<template>
  <div class="profile-view">
    <h2>个人资料</h2>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="头像">
        <div class="avatar-upload">
          <el-upload class="avatar-uploader" :show-file-list="false" :auto-upload="false"
            :on-change="handleAvatarChange" accept="image/jpeg,image/png">
            <el-avatar :size="100" :src="avatarUrl" class="avatar-preview">
              <el-icon class="avatar-icon">
                <Plus />
              </el-icon>
            </el-avatar>
          </el-upload>
          <el-button :disabled="!isAvatarChanged" size="small" type="primary" @click="uploadAvatar">
            上传头像
          </el-button>
        </div>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input :value="authStore.currentUser?.username" disabled />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <div class="email-input-container">
          <el-input v-model="form.email" />
          <el-button v-if="isEmailChanged" @click="sendVerificationCode" :disabled="!authStore.canSendCode">
            {{ authStore.isCodeSent ? `重新发送(${authStore.countdown}s)` : '发送验证码' }}
          </el-button>
        </div>
      </el-form-item>
      <el-form-item v-if="isEmailChanged" label="邮箱验证码" prop="emailVerificationCode">
        <el-input v-model="form.emailVerificationCode" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item v-if="showPasswordFields" label="确认新密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" show-password />
      </el-form-item>
      <el-form-item v-if="showPasswordFields" label="旧密码" prop="oldPassword" required>
        <el-input v-model="form.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleUpdate">更新资料</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage, ElAvatar, ElUpload, ElIcon } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import type { UserUpdateDto } from '@/service/api'
import {
  profileEmailRules,
  profilePasswordRules,
  profileConfirmPasswordRules,
  profileVerificationCodeRules,
} from '@/utils/validationRules'
import type { UploadFile } from 'element-plus'
import { imageValidationRules } from '@/utils/validationRules'

const authStore = useAuthStore()
const formRef = ref()

const form = ref<UserUpdateDto & { confirmPassword: string }>({
  email: authStore.currentUser?.email || '',
  oldPassword: '',
  newPassword: '',
  emailVerificationCode: '',
  confirmPassword: '',
})

const avatarUrl = ref(authStore.currentUser?.avatar || '')
const isAvatarChanged = ref(false)
const selectedFile = ref<File | null>(null)

const originalEmail = ref(authStore.currentUser?.email || '')
const isEmailChanged = computed(() => form.value.email !== originalEmail.value)
const showPasswordFields = computed(() => !!form.value.newPassword)

const rules = {
  email: profileEmailRules,
  newPassword: profilePasswordRules,
  confirmPassword: profileConfirmPasswordRules(() => form.value.newPassword || ''),
  emailVerificationCode: profileVerificationCodeRules,
}

const handleAvatarValidation = (file: UploadFile): boolean => {
  if (!file.raw) {
    ElMessage.error('上传的文件无效')
    return false
  }
  const validationResult = imageValidationRules.validateImage(file.raw)
  if (typeof validationResult === 'string') {
    ElMessage.error(validationResult)
    return false
  }
  return true
}

const handleAvatarChange = (file: UploadFile) => {
  if (!handleAvatarValidation(file)) {
    return false
  }

  if (file.raw && file.raw instanceof File) {
    selectedFile.value = file.raw
    avatarUrl.value = URL.createObjectURL(file.raw)
    isAvatarChanged.value = true
  } else {
    ElMessage.error('上传的文件无效')
  }
}

const uploadAvatar = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择头像图片')
    return
  }

  try {
    const result = await authStore.uploadUserAvatar(selectedFile.value)
    if (result) {
      ElMessage.success('头像上传成功')
      isAvatarChanged.value = false
    } else {
      ElMessage.error('头像上传失败')
    }
  } catch (error) {
    console.error('上传头像时出错:', error)
    ElMessage.error('上传头像时出错')
  }
}

const handleUpdate = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const updateData = { ...form.value } as Partial<typeof form.value>;
        delete updateData.confirmPassword;
        updateData.newPassword ??= undefined;
        updateData.oldPassword ??= undefined;
        const updatedUser = await authStore.updateProfile(updateData)
        if (updatedUser) {
          ElMessage.success('个人资料更新成功')
          // 重置密码相关字段
          form.value.oldPassword = ''
          form.value.newPassword = ''
          form.value.confirmPassword = ''
        } else {
          ElMessage.error('个人资料更新失败')
        }
      } catch (error) {
        console.error('更新个人资料时出错:', error)
        ElMessage.error('更新个人资料时出错')
      }
    } else {
      ElMessage.error('请正确填写所有必填字段')
    }
  })
}

const sendVerificationCode = async () => {
  if (!form.value.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  try {
    const result = await authStore.sendVerificationCodeToEmail(form.value.email)
    if (result && result.code === 200) {
      ElMessage.success('验证码已发送，请查收邮箱')
      // 不需要手动调用 startCountdown，因为它现在在 auth store 中自动处理
    } else {
      ElMessage.error(result?.message || '发送验证码失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error('发送验证码失败，请稍后重试')
  }
}
</script>

<style scoped>
.profile-view {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}

.avatar-upload {
  display: flex;
  align-items: center;
}

.avatar-uploader {
  margin-right: 20px;
}

.avatar-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  cursor: pointer;
  background-color: #f0f0f0;
}

.avatar-icon {
  font-size: 24px;
  color: #909399;
}

.avatar-preview:hover .avatar-icon {
  color: #409EFF;
}

:deep(.el-form-item__label) {
  font-weight: bold;
}

.email-input-container {
  display: flex;
  align-items: center;
}

.email-input-container .el-input {
  flex: 1;
  margin-right: 10px;
}

.email-input-container .el-button {
  flex-shrink: 0;
}

:deep(.el-input) {
  width: 100%;
}
</style>
