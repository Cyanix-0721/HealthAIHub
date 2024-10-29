import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  login,
  logout,
  checkLoginStatus,
  register,
  sendEmailCode,
  verifyEmailCode,
  getUserInfo,
  resetPassword,
  updateUserProfile,
  uploadAvatar,
} from '@/service/api'
import type {
  UserLoginDto,
  UserRegisterDto,
  EmailDto,
  ResetPasswordDto,
  UserDto,
  UserUpdateDto,
} from '@/service/api'
import type { PiniaPluginContext } from 'pinia'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore(
  'auth',
  () => {
    const token = ref<string | null>(localStorage.getItem('token'))
    const isLoggedIn = ref(!!token.value)
    const countdown = ref(0)
    const isCodeSent = ref(false)
    const currentUser = ref<UserDto | null>(null)

    const canSendCode = computed(
      () => !isCodeSent.value || countdown.value === 0,
    )

    const setAuthState = (newToken: string) => {
      token.value = newToken
      localStorage.setItem('token', newToken)
      console.log('setAuthState:', newToken)
      isLoggedIn.value = true
    }

    const clearAuthState = () => {
      token.value = null
      currentUser.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('currentUser')
      localStorage.removeItem('auth-store')
      isLoggedIn.value = false
    }

    const initializeAuth = async () => {
      resetVerificationCodeState()
      if (token.value) {
        console.log('Auth initialized')
        if (!currentUser.value) {
          try {
            await fetchCurrentUserInfo()
          } catch (error) {
            console.warn('初始化认证时无法获取用户信息:', error)
            // 不要在这里清除认证状态，而是尝试使用token重新获取用户信息
            try {
              const result = await getUserInfo()
              if (result.code === 200 && result.data) {
                currentUser.value = result.data
                localStorage.setItem('currentUser', JSON.stringify(result.data))
              } else {
                console.error('使用token获取用户信息失败:', result)
                clearAuthState()
              }
            } catch (tokenError) {
              console.error('使用token获取用户信息失败:', tokenError)
              clearAuthState()
            }
          }
        }
      }
    }

    const loginUser = async (credentials: UserLoginDto) => {
      try {
        console.log('开始登录，发送的凭证:', credentials)
        const result = await login(credentials)
        console.log('登录结果:', result)
        if (result.code === 200 && result.data) {
          setAuthState(result.data)
          try {
            await fetchCurrentUserInfo(
              credentials.username || credentials.email,
            )
            console.log('auth: 获取用户信息成功')
            return true
          } catch (error) {
            console.error('auth: 获取用户信息失败:', error)
            return false
          }
        }
        return false
      } catch (error) {
        console.error('登录失败:', error)
        clearAuthState()
        throw error
      }
    }

    const registerUser = async (userData: UserRegisterDto) => {
      const result = await register(userData)
      if (result.code === 200 && result.data) {
        setAuthState(result.data)
        isLoggedIn.value = true
      }
      return result
    }

    const sendVerificationCodeToEmail = async (email: string) => {
      if (!canSendCode.value) return
      resetVerificationCodeState()
      try {
        const emailDto: EmailDto = { email }
        const result = await sendEmailCode(emailDto)
        if (result.code === 200) {
          await new Promise(resolve => setTimeout(resolve, 1000))
          isCodeSent.value = true
          startCountdown()
        }
        return result
      } catch (error) {
        console.error('发送邮箱验证码失败:', error)
        throw error
      }
    }

    const verifyEmailVerificationCode = async (
      email: string,
      emailVerificationCode: string,
    ) => {
      try {
        const emailDto: EmailDto = { email, emailVerificationCode }
        const result = await verifyEmailCode(emailDto)
        return result
      } catch (error) {
        console.error('邮箱验证码验证失败:', error)
        throw error
      }
    }

    const startCountdown = () => {
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value === 0) {
          clearInterval(timer)
          isCodeSent.value = false
        }
      }, 1000)
    }

    const checkAuth = async () => {
      const result = await checkLoginStatus()
      isLoggedIn.value = result.code === 200 && result.data === true
      return isLoggedIn.value
    }

    const logoutUser = async () => {
      try {
        const result = await logout()
        if (result.code === 200) {
          clearAuthState()
          isLoggedIn.value = false
          currentUser.value = null
          resetVerificationCodeState()
        }
        return result
      } catch (error) {
        console.error('登出失败:', error)
        throw error
      }
    }

    const checkUsernameEmailAvailability = async (
      username?: string,
      email?: string,
    ) => {
      try {
        const result = await getUserInfo(username, email)
        const isAvailable = result.code === 500
        return {
          code: result.code,
          message: isAvailable ? '用户名或邮箱可用' : '用户名或邮箱已被使用',
          data: isAvailable,
        }
      } catch (error) {
        console.error('检查用户名和邮箱可用性失败:', error)
        return {
          code: 500,
          message: '检查失败',
          data: false,
        }
      }
    }

    const fetchCurrentUserInfo = async (identifier?: string) => {
      try {
        let result
        if (identifier) {
          const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(identifier)
          result = await getUserInfo(
            isEmail ? '' : identifier,
            isEmail ? identifier : '',
          )
        } else {
          const storedUser = localStorage.getItem('currentUser')
          if (storedUser) {
            console.log('检测到存储的用户信息')
            const parsedUser = JSON.parse(storedUser)
            result = await getUserInfo(
              parsedUser.username || '',
              parsedUser.email || '',
            )
            console.log('parsedUser: ', parsedUser)
          } else {
            // 如果没有存储的用户信息，尝试使用token获取
            result = await getUserInfo()
          }
        }

        console.log('获取当前用户信息结果:', result)
        if (result.code === 200 && result.data) {
          currentUser.value = result.data
          localStorage.setItem('currentUser', JSON.stringify(result.data))
          isLoggedIn.value = true
        } else {
          console.error('获取用户信息失败:', result)
          throw new Error(result.message || '获取用户信息失败')
        }
      } catch (error) {
        console.error('获取当前用户信息失败:', error)
        throw error
      }
    }

    const resetUserPassword = async (
      email: string,
      newPassword: string,
      emailVerificationCode: string,
    ) => {
      try {
        const resetPasswordDto: ResetPasswordDto = {
          email,
          newPassword,
          emailVerificationCode,
        }
        return await resetPassword(resetPasswordDto)
      } catch (error) {
        console.error('重置密码失败:', error)
        throw error
      }
    }

    const updateProfile = async (
      userUpdateDto: UserUpdateDto,
    ): Promise<UserDto | null> => {
      try {
        console.log('即将发送userUpdateDto: ', userUpdateDto)

        const result = await updateUserProfile(userUpdateDto)
        console.log(result)
        if (result.code === 200 && result.data) {
          currentUser.value = result.data
          return result.data
        }
        return null
      } catch (error) {
        console.error('更新用户资料失败:', error)
        throw error
      }
    }

    const uploadUserAvatar = async (file: File): Promise<string | null> => {
      try {
        const result = await uploadAvatar(file)
        if (result.code === 200 && result.data) {
          if (currentUser.value) {
            currentUser.value.avatar = result.data
            console.log('上传头像成功，更新用户头像:', currentUser.value.avatar)
          }
          return result.data
        }
        console.error('上传头像失败，服务器返回:', result)
        return null
      } catch (error) {
        console.error('上传头像失败:', error)
        if (error instanceof Error) {
          ElMessage.error(`上传头像失败: ${error.message}`)
        } else {
          ElMessage.error('上传头像失败，请稍后重试')
        }
        throw error
      }
    }

    const resetVerificationCodeState = () => {
      isCodeSent.value = false
      countdown.value = 0
    }

    return {
      token,
      isLoggedIn,
      countdown,
      isCodeSent,
      canSendCode,
      sendVerificationCodeToEmail,
      startCountdown,
      setAuthState,
      clearAuthState,
      loginUser,
      registerUser,
      verifyEmailVerificationCode,
      checkAuth,
      logoutUser,
      checkUsernameEmailAvailability,
      resetUserPassword,
      updateProfile,
      uploadUserAvatar,
      currentUser,
      fetchCurrentUserInfo,
      initializeAuth,
      resetVerificationCodeState,
    }
  },
  {
    persist: {
      key: 'auth-store',
      storage: localStorage,
      paths: ['token', 'isLoggedIn', 'currentUser'],
    } as PiniaPluginContext['options']['persist'],
  },
)
