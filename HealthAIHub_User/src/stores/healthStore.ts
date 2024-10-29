import { defineStore } from 'pinia'
import { ref } from 'vue'
import { healthApi } from '@/service/healthApi'
import type { HealthDataSaveDto, HealthDataDto } from '@/service/healthApi'
import type { CommonResult } from '@/service/api'
import { useAuthStore } from './auth'
import type { PiniaPluginContext } from 'pinia'

export const useHealthStore = defineStore(
  'health',
  () => {
    const healthDataResult = ref<HealthDataDto | null>(null)

    const updateHealthData = async (
      data: HealthDataSaveDto,
    ): Promise<CommonResult<boolean>> => {
      console.log('开始更新健康数据:', data)
      const result = await healthApi.insertCurrentUserHealthData(data)
      console.log('更新健康数据结果:', result)
      if (result.code === 200 && result.data) {
        console.log('更新成功，开始获取最新健康数据')
        await getCurrentUserHealthData()
      }
      return result
    }

    const getCurrentUserHealthData = async (): Promise<
      CommonResult<HealthDataDto>
    > => {
      const authStore = useAuthStore()
      const currentUserId = authStore.currentUser?.id
      if (!currentUserId) {
        throw new Error('无法获取当前用户ID')
      }
      console.log(`获取用户ID ${currentUserId} 的健康数据`)
      const result = await healthApi.getByUserId(currentUserId.toString())
      console.log('获取健康数据结果:', result)
      if (result.code === 200 && result.data) {
        healthDataResult.value = result.data
        console.log('healthDataResult 已更新:', healthDataResult.value)
      } else {
        console.error('获取健康数据失败:', result.message)
      }
      return result
    }

    const initializeHealthData = async () => {
      try {
        await getCurrentUserHealthData()
      } catch (error) {
        console.error('初始化健康数据失败:', error)
      }
    }

    return {
      healthDataResult,
      updateHealthData,
      getCurrentUserHealthData,
      initializeHealthData,
    }
  },
  {
    persist: {
      key: 'health-store',
      storage: localStorage,
      paths: ['healthDataResult'],
    } as PiniaPluginContext['options']['persist'],
  },
)
