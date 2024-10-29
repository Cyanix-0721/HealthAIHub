<template>
  <div class="health-data-view">
    <h1>健康数据</h1>
    <el-form :model="localHealthData" :rules="healthDataRules" label-width="100px" hide-required-asterisk>
      <el-form-item label="身高 (cm)" prop="height">
        <el-input-number v-model="localHealthData.height" :min="50" :max="300" :precision="1" />
      </el-form-item>
      <el-form-item label="体重 (kg)">
        <el-input-number v-model="localHealthData.weight" :min="0" :max="500" :precision="1" />
      </el-form-item>
      <el-form-item label="心率 (次/分)">
        <el-input-number v-model="localHealthData.heartRate" :min="0" :max="300" :precision="0" />
      </el-form-item>
      <el-form-item label="血压 (mmHg)">
        <el-input v-model="localHealthData.bloodPressure" placeholder="例如: 120/80" />
      </el-form-item>
      <el-form-item label="血氧 (%)">
        <el-input-number v-model="localHealthData.bloodOxygen" :min="0" :max="100" :precision="0" />
      </el-form-item>
      <el-form-item label="血糖 (mmol/L)">
        <el-input-number v-model="localHealthData.bloodSugar" :min="0" :max="30" :precision="1" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updateHealthData">更新健康数据</el-button>
        <el-dropdown @command="handleShare" trigger="hover">
          <el-button type="success">
            分享健康数据
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="copy">复制健康数据</el-dropdown-item>
              <el-dropdown-item command="assistant">分享到个人健康助手</el-dropdown-item>
              <el-dropdown-item command="friends">分享到好友</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { useHealthStore } from '@/stores/healthStore'
import { useAuthStore } from '@/stores/auth'
import { healthDataRules } from '@/utils/validationRules'
import type { HealthDataSaveDto, HealthDataDto } from '@/service/healthApi'

const healthStore = useHealthStore()
const authStore = useAuthStore()
const { healthDataResult } = storeToRefs(healthStore)

const localHealthData = ref<HealthDataSaveDto>({
  height: 0,
  weight: 0,
  heartRate: 0,
  bloodPressure: '',
  bloodOxygen: 0,
  bloodSugar: 0,
})

watch(
  () => healthDataResult.value,
  (newValue: HealthDataDto | null) => {
    console.log('watch 检测到 healthDataResult 变化:', newValue)
    if (newValue) {
      localHealthData.value = {
        height: newValue.height,
        weight: newValue.weight,
        heartRate: newValue.heartRate,
        bloodPressure: newValue.bloodPressure,
        bloodOxygen: newValue.bloodOxygen,
        bloodSugar: newValue.bloodSugar,
      }
      console.log('localHealthData 已更新:', localHealthData.value)
    }
  },
  { deep: true, immediate: true },
)

onMounted(async () => {
  try {
    await authStore.initializeAuth()
    await healthStore.initializeHealthData()
    console.log('初始化健康数据完成')
  } catch (error) {
    ElMessage.error(`获取健康数据失败: ${(error as Error).message}`)
  }
})

const updateHealthData = async () => {
  try {
    console.log('开始更新健康数据:', localHealthData.value)
    const updateResult = await healthStore.updateHealthData(localHealthData.value)
    console.log('更新健康数据结果:', updateResult)
    if (updateResult.code === 200 && updateResult.data) {
      ElMessage.success('健康数据更新成功')
    } else {
      ElMessage.error(`健康数据更新失败: ${updateResult.message}`)
    }
  } catch (error) {
    ElMessage.error(`健康数据更新失败: ${(error as Error).message}`)
  }
}

const handleShare = async (command: string) => {
  if (!healthDataResult.value) {
    ElMessage.error('没有可分享的健康数据')
    return
  }

  const healthDataString = `身高: ${healthDataResult.value.height}cm, 体重: ${healthDataResult.value.weight}kg, 心率: ${healthDataResult.value.heartRate}次/分, 血压: ${healthDataResult.value.bloodPressure}, 血氧: ${healthDataResult.value.bloodOxygen}%, 血糖: ${healthDataResult.value.bloodSugar}mmol/L`

  console.log(`准备分享健康数据: ${healthDataString}`)

  try {
    switch (command) {
      case 'copy':
        await navigator.clipboard.writeText(healthDataString)
        ElMessage.success('健康数据已复制到剪贴板')
        break
      case 'assistant':
        ElMessage.info('分享到个人健康助手功能尚未实现')
        break
      case 'friends':
        ElMessage.info('分享到好友功能尚未实现')
        break
    }
  } catch (error) {
    ElMessage.error(`分享失败: ${(error as Error).message}`)
  }
}
</script>

<style scoped>
.health-data-view {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  margin-bottom: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-dropdown {
  margin-left: 10px;
}
</style>
