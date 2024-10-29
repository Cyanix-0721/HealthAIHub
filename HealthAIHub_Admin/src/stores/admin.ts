import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getAdminList,
  batchSaveOrUpdateAdmins,
  batchDeleteAdmins,
} from '@/service/adminApi'
import type { Admin, AdminSaveDto } from '@/service/adminApi'

export const useAdminStore = defineStore('admin', () => {
  const admins = ref<Admin[]>([])
  const totalAdmins = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  const fetchAdmins = async (username?: string) => {
    try {
      const result = await getAdminList(
        currentPage.value,
        pageSize.value,
        username,
      )
      if (result.code === 200 && result.data) {
        admins.value = result.data.list
        totalAdmins.value = result.data.total
      } else {
        throw new Error(result.message || '获取管理员列表失败')
      }
    } catch (error) {
      console.error('获取管理员列表失败:', error)
    }
  }

  const saveOrUpdateAdmin = async (admin: AdminSaveDto) => {
    try {
      const result = await batchSaveOrUpdateAdmins([admin])
      if (result.code === 200 && result.data) {
        await fetchAdmins()
        return true
      } else {
        throw new Error(result.message || '保存管理员失败')
      }
    } catch (error) {
      console.error('保存管理员失败:', error)
      throw error
    }
  }

  const deleteAdmins = async (ids: number[]) => {
    try {
      const result = await batchDeleteAdmins(ids)
      if (result.code === 200 && result.data) {
        await fetchAdmins()
        return true
      } else {
        throw new Error(result.message || '删除管理员失败')
      }
    } catch (error) {
      console.error('删除管理员失败:', error)
      throw error
    }
  }

  return {
    admins,
    totalAdmins,
    currentPage,
    pageSize,
    fetchAdmins,
    saveOrUpdateAdmin,
    deleteAdmins,
  }
})
