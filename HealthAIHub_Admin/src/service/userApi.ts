import { api } from './api'
import type { CommonResult, CommonPage } from './api'

export interface User {
  id: number
  username: string
  password: string
  avatar?: string
  email: string
  createdAt: string
}

export interface UserSaveDto {
  id?: number; // 如果是编辑，会有id；如果是新增，则没有id
  username: string;
  email: string;
  password?: string;
}

export const getUserList = async (
  pageNum: number = 1,
  pageSize: number = 10,
  username?: string,
): Promise<CommonResult<CommonPage<User>>> => {
  const params = new URLSearchParams({
    pageNum: pageNum.toString(),
    pageSize: pageSize.toString(),
  })
  if (username) {
    params.append('username', username)
  }
  const response = await api.get<CommonResult<CommonPage<User>>>('/user/list', {
    params,
  })
  return response.data
}

export const batchSaveOrUpdateUsers = async (
  userSaveDtoList: UserSaveDto[],
): Promise<CommonResult<boolean>> => {
  const response = await api.post<CommonResult<boolean>>(
    '/user/batch',
    userSaveDtoList,
  )
  return response.data
}

export const batchDeleteUsers = async (
  ids: number[],
): Promise<CommonResult<boolean>> => {
  const response = await api.delete<CommonResult<boolean>>('/user/batch', {
    data: ids,
  })
  return response.data
}
