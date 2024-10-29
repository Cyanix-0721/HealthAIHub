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

// 健康数据验证规则
export const healthDataRules: Record<string, FormItemRule[]> = {
  height: [
    { required: true, message: '身高不能为空', trigger: 'blur' },
    {
      type: 'number',
      min: 50,
      max: 300,
      message: '身高必须在50cm到300cm之间',
      trigger: 'blur',
    },
  ],
  weight: [
    { required: true, message: '体重不能为空', trigger: 'blur' },
    {
      type: 'number',
      min: 2,
      max: 500,
      message: '体重必须在2kg到500kg之间',
      trigger: 'blur',
    },
  ],
  heartRate: [
    { required: true, message: '心率不能为空', trigger: 'blur' },
    {
      type: 'number',
      min: 30,
      max: 220,
      message: '心率必须在30次/分到220次/分之间',
      trigger: 'blur',
    },
  ],
  bloodPressure: [
    { required: true, message: '血压不能为空', trigger: 'blur' },
    {
      pattern: /^\d{2,3}\/\d{2,3}$/,
      message: '血压格式不正确，应为xxx/xxx',
      trigger: 'blur',
    },
  ],
  bloodOxygen: [
    { required: true, message: '血氧饱和度不能为空', trigger: 'blur' },
    {
      type: 'number',
      min: 50,
      max: 100,
      message: '血氧饱和度必须在50%到100%之间',
      trigger: 'blur',
    },
  ],
  bloodSugar: [
    { required: true, message: '血糖不能为空', trigger: 'blur' },
    {
      type: 'number',
      min: 1,
      max: 30,
      message: '血糖必须在1mmol/L到30mmol/L之间',
      trigger: 'blur',
    },
  ],
}
