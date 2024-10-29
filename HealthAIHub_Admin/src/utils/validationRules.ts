import type { FormItemRule } from 'element-plus'

export const usernameRules: FormItemRule[] = [
  { required: true, message: '请输入用户名', trigger: 'blur' },
  {
    pattern: /^[a-z0-9]{1,10}$/,
    message: '用户名只能包含小写字母和数字，且长度不超过10位',
    trigger: 'blur',
  },
]

export const passwordRules: FormItemRule[] = [
  { required: true, message: '请输入密码', trigger: 'blur' },
  {
    pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,20}$/,
    message: '密码必须包含大小写字母和数字，长度在8-20位之间',
    trigger: 'blur',
  },
]

export const confirmPasswordRules = (
  getPassword: () => string,
): FormItemRule[] => [
  { required: true, message: '请确认密码', trigger: 'blur' },
  {
    validator: (_, value, callback) => {
      if (value !== getPassword()) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    },
    trigger: 'blur',
  },
]

export const emailRules: FormItemRule[] = [
  { required: true, message: '请输入邮箱地址', trigger: 'blur' },
  { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' },
]

export const verificationCodeRules: FormItemRule[] = [
  { required: true, message: '请输入验证码', trigger: 'blur' },
  { pattern: /^\d{6}$/, message: '验证码必须是6位数字', trigger: 'blur' },
]

// 登录专用规则
export const loginUsernameRules: FormItemRule[] = [
  { required: true, message: '请输入用户名', trigger: 'blur' },
]

export const loginPasswordRules: FormItemRule[] = [
  { required: true, message: '请输入密码', trigger: 'blur' },
]

// 个人中心专用规则
export const profileEmailRules: FormItemRule[] = emailRules.map(rule => ({
  ...rule,
  required: false,
}))

export const profilePasswordRules: FormItemRule[] = passwordRules.map(rule => ({
  ...rule,
  required: false,
}))

export const profileConfirmPasswordRules = (
  getPassword: () => string,
): FormItemRule[] => [
  {
    validator: (_, value, callback) => {
      const password = getPassword()
      if (password && !value) {
        callback(new Error('请确认新密码'))
      } else if (password && value !== password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    },
    trigger: 'blur',
  },
]

export const profileVerificationCodeRules: FormItemRule[] =
  verificationCodeRules.map(rule => ({ ...rule, required: true }))

export const oldPasswordRules = (
  showPasswordFields: () => boolean,
): FormItemRule[] => [
  {
    required: true,
    message: '请输入旧密码',
    trigger: 'blur',
    validator: (rule, value, callback) => {
      if (showPasswordFields() && !value) {
        callback(new Error('请输入旧密码'))
      } else {
        callback()
      }
    },
  },
]

// 图片验证规则
export const imageValidationRules = {
  validateImageType: (file: File): boolean => {
    const isJPG = file.type === 'image/jpeg'
    const isPNG = file.type === 'image/png'
    return isJPG || isPNG
  },
  validateImageSize: (file: File): boolean => {
    const isLt5M = file.size / 1024 / 1024 < 5
    return isLt5M
  },
  validateImage: (file: File): string | boolean => {
    if (!imageValidationRules.validateImageType(file)) {
      return '头像图片只能是 JPG 或 PNG 格式!'
    }
    if (!imageValidationRules.validateImageSize(file)) {
      return '头像图片大小不能超过 5MB!'
    }
    return true
  },
}
