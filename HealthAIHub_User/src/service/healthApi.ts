import { api } from './api'
import type { CommonResult } from './api'

export interface HealthDataSaveDto {
  height: number
  weight: number
  heartRate: number
  bloodPressure: string
  bloodOxygen: number
  bloodSugar: number
}

export interface HealthDataDto extends HealthDataSaveDto {
  updatedAt: string
  bmi: number
  healthStatus: string
}

export const healthApi = {
  getByUserId: async (userId: string): Promise<CommonResult<HealthDataDto>> => {
    const response = await api.get<CommonResult<HealthDataDto>>(
      `/health-data/${userId}`,
    )
    return response.data
  },

  insertCurrentUserHealthData: async (
    data: HealthDataSaveDto,
  ): Promise<CommonResult<boolean>> => {
    const response = await api.post<CommonResult<boolean>>(
      '/health-data/current',
      data,
    )
    return response.data
  },
}
