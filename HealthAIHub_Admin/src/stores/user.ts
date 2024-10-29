import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getUserList,
  batchSaveOrUpdateUsers,
  batchDeleteUsers,
} from '@/service/userApi'
import type { User, UserSaveDto } from '@/service/userApi'

export const useUserStore = defineStore('user', () => {
  const users = ref<User[]>([])
  const totalUsers = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  const fetchUsers = async (username?: string) => {
    try {
      const result = await getUserList(
        currentPage.value,
        pageSize.value,
        username,
      )
      if (result.code === 200 && result.data) {
        users.value = result.data.list
        totalUsers.value = result.data.total
      } else {
        throw new Error(result.message || '获取用户列表失败')
      }
    } catch (error) {
      console.error('获取用户列表失败:', error)
    }
  }

  const saveOrUpdateUser = async (user: UserSaveDto) => {
    try {
      const result = await batchSaveOrUpdateUsers([user])
      if (result.code === 200 && result.data) {
        await fetchUsers()
        return true
      } else {
        throw new Error(result.message || '保存用户失败')
      }
    } catch (error) {
      console.error('保存用户失败:', error)
      throw error
    }
  }

  const deleteUsers = async (ids: number[]) => {
    try {
      const result = await batchDeleteUsers(ids)
      if (result.code === 200 && result.data) {
        await fetchUsers()
        return true
      } else {
        throw new Error(result.message || '删除用户失败')
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      throw error
    }
  }

  return {
    users,
    totalUsers,
    currentPage,
    pageSize,
    fetchUsers,
    saveOrUpdateUser,
    deleteUsers,
  }
})
