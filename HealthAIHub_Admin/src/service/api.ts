import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios'

const api: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    console.log('Request headers:', config.headers)
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

export interface CommonPage<T> {
  pageNum: number
  pageSize: number
  totalPage: number
  total: number
  list: T[]
}

export enum LoginType {
  USERNAME = 'USERNAME',
  EMAIL = 'EMAIL',
  EMAIL_VERIFICATION_CODE = 'EMAIL_VERIFICATION_CODE',
}
export interface AdminLoginDto {
  username?: string
  email?: string
  password?: string
  emailVerificationCode?: string
  loginType: LoginType
}

export const login = async (
  credentials: AdminLoginDto,
): Promise<CommonResult<string>> => {
  const response = await api.post<CommonResult<string>>(
    '/admin/login',
    credentials,
  )
  return response.data
}

export interface AdminRegisterDto {
  username: string
  email: string
  password: string
  emailVerificationCode: string
}

export const register = async (
  userData: AdminRegisterDto,
): Promise<CommonResult<string>> => {
  const response = await api.post<CommonResult<string>>(
    '/admin/register',
    userData,
  )
  return response.data
}

export const logout = async (): Promise<CommonResult<string>> => {
  const response = await api.post<CommonResult<string>>('/admin/logout')
  return response.data
}

export const checkLoginStatus = async (): Promise<CommonResult<boolean>> => {
  const response = await api.get<CommonResult<boolean>>(
    '/admin/check-login-status',
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

export interface AdminDto {
  id?: number
  username?: string
  email?: string
  avatar?: string
}

export const getAdminInfo = async (
  username?: string,
  email?: string,
): Promise<CommonResult<AdminDto | null>> => {
  const params = new URLSearchParams()
  if (username) params.append('username', username)
  if (email) params.append('email', email)

  const response = await api.get<CommonResult<AdminDto | null>>(
    '/admin/get-admin-info',
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
    '/admin/reset-password',
    resetPasswordDto,
  )
  return response.data
}

export interface AdminUpdateDto {
  username?: string
  email?: string
  emailVerificationCode?: string
  oldPassword?: string
  newPassword?: string
}

export const updateAdminProfile = async (
  adminUpdateDto: AdminUpdateDto,
): Promise<CommonResult<AdminDto>> => {
  const response = await api.post<CommonResult<AdminDto>>(
    '/admin/update-admin-profile',
    adminUpdateDto,
  )
  return response.data
}

export const uploadAvatar = async (
  file: File,
): Promise<CommonResult<string>> => {
  const formData = new FormData()
  formData.append('file', file)
  const response = await api.post<CommonResult<string>>(
    '/admin/upload-avatar',
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
