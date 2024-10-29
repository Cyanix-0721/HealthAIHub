import { api } from './api'
import type { CommonResult, CommonPage } from './api'

export interface Admin {
  id: number
  username: string
  password: string
  avatar?: string
  email: string
  createdAt: string
}

export interface AdminSaveDto {
  id?: number // 如果是编辑，会有id；如果是新增，则没有id
  username: string
  email: string
  password?: string
}

/**
 * 获取管理员列表，支持用户名模糊搜索
 *
 * @param pageNum 页码
 * @param pageSize 每页大小
 * @param username 用户名（可选，用于模糊搜索）
 * @returns 管理员列表
 */
export const getAdminList = async (
  pageNum: number = 1,
  pageSize: number = 10,
  username?: string,
): Promise<CommonResult<CommonPage<Admin>>> => {
  const params = new URLSearchParams({
    pageNum: pageNum.toString(),
    pageSize: pageSize.toString(),
  })
  if (username) {
    params.append('username', username)
  }
  const response = await api.get<CommonResult<CommonPage<Admin>>>(
    '/admin/list',
    { params },
  )
  return response.data
}

/**
 * 批量添加或更新管理员
 *
 * @param adminSaveDtoList 管理员存储数据传输对象列表
 * @returns 是否操作成功
 */
export const batchSaveOrUpdateAdmins = async (
  adminSaveDtoList: AdminSaveDto[],
): Promise<CommonResult<boolean>> => {
  const response = await api.post<CommonResult<boolean>>(
    '/admin/batch',
    adminSaveDtoList,
  )
  return response.data
}

/**
 * 批量删除管理员
 *
 * @param ids 管理员ID列表
 * @returns 是否删除成功
 */
export const batchDeleteAdmins = async (
  ids: number[],
): Promise<CommonResult<boolean>> => {
  const response = await api.delete<CommonResult<boolean>>('/admin/batch', {
    data: ids,
  })
  return response.data
}
