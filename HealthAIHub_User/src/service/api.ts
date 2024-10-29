import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios'

const api: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_PATH || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

console.log('API baseURL:', api.defaults.baseURL)
console.log('api.ts is being executed')
console.log('Current environment:', import.meta.env.MODE)
console.log('VITE_BACKEND_URL:', import.meta.env.VITE_BACKEND_URL)

api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    console.log('Request headers:', config.headers)
    console.log('Request URL:', config.url)
    console.log('Full URL:', `${config.baseURL}${config.url}`)
    return config
  },
  error => {
    return Promise.reject(error)
  },
)

export interface CommonResult<T> {
  code: number
  message: string
  data: T | null
}

export enum LoginType {
  USERNAME = 'USERNAME',
  EMAIL = 'EMAIL',
  EMAIL_VERIFICATION_CODE = 'EMAIL_VERIFICATION_CODE',
}
export interface UserLoginDto {
  username?: string
  email?: string
  password?: string
  emailVerificationCode?: string
  loginType: LoginType
}

export const login = async (
  credentials: UserLoginDto,
): Promise<CommonResult<string>> => {
  const response = await api.post<CommonResult<string>>(
    '/user/login',
    credentials,
  )
  return response.data
}
export interface UserRegisterDto {
  username: string
  email: string
  password: string
  emailVerificationCode: string
}
export const register = async (
  userData: UserRegisterDto,
): Promise<CommonResult<string>> => {
  const response = await api.post<CommonResult<string>>(
    '/user/register',
    userData,
  )
  return response.data
}

export const logout = async (): Promise<CommonResult<string>> => {
  const response = await api.post<CommonResult<string>>('/user/logout')
  return response.data
}

export const checkLoginStatus = async (): Promise<CommonResult<boolean>> => {
  const response = await api.get<CommonResult<boolean>>(
    '/user/check-login-status',
  )
  return response.data
}

export interface EmailDto {
  email: string
  emailVerificationCode?: string
}

export const sendEmailCode = async (
  emailDto: EmailDto,
): Promise<CommonResult<string>> => {
  const response = await api.post<CommonResult<string>>(
    '/email-verification/send-code',
    emailDto,
  )
  return response.data
}

export const verifyEmailCode = async (
  emailDto: EmailDto,
): Promise<CommonResult<boolean>> => {
  const response = await api.post<CommonResult<boolean>>(
    '/email-verification/verify-code',
    emailDto,
  )
  return response.data
}

export interface UserDto {
  id?: number
  username?: string
  email?: string
  avatar?: string
}

export const getUserInfo = async (
  username?: string,
  email?: string,
): Promise<CommonResult<UserDto | null>> => {
  const params = new URLSearchParams()
  if (username) params.append('username', username)
  if (email) params.append('email', email)

  const response = await api.get<CommonResult<UserDto | null>>(
    '/user/get-user-info',
    { params },
  )
  return response.data
}

export interface ResetPasswordDto {
  email: string
  newPassword: string
  emailVerificationCode: string
}

export const resetPassword = async (
  resetPasswordDto: ResetPasswordDto,
): Promise<CommonResult<string>> => {
  const response = await api.post<CommonResult<string>>(
    '/user/reset-password',
    resetPasswordDto,
  )
  return response.data
}

export interface UserUpdateDto {
  username?: string
  email?: string
  emailVerificationCode?: string
  oldPassword?: string
  newPassword?: string
}

export const updateUserProfile = async (
  userUpdateDto: UserUpdateDto,
): Promise<CommonResult<UserDto>> => {
  const response = await api.post<CommonResult<UserDto>>(
    '/user/update-user-profile',
    userUpdateDto,
  )
  return response.data
}

export const uploadAvatar = async (
  file: File,
): Promise<CommonResult<string>> => {
  const formData = new FormData()
  formData.append('file', file)
  const response = await api.post<CommonResult<string>>(
    '/user/upload-avatar',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    },
  )
  return response.data
}

export { api }
