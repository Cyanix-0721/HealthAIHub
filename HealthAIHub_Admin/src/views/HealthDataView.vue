<template>
  <div class="health-data-view">
    <h1>健康数据</h1>
    <el-form :model="healthData" label-width="100px">
      <el-form-item label="身高 (cm)">
        <el-input-number v-model="healthData.height" :min="0" :max="300" :precision="1" />
      </el-form-item>
      <el-form-item label="体重 (kg)">
        <el-input-number v-model="healthData.weight" :min="0" :max="500" :precision="1" />
      </el-form-item>
      <el-form-item label="心率 (次/分)">
        <el-input-number v-model="healthData.heartRate" :min="0" :max="300" :precision="0" />
      </el-form-item>
      <el-form-item label="血压 (mmHg)">
        <el-input v-model="healthData.bloodPressure" placeholder="例如: 120/80" />
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
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const healthData = reactive({
  height: 170,
  weight: 60,
  heartRate: 75,
  bloodPressure: '120/80'
})

const updateHealthData = async () => {
  try {
    // 这里应该调用API来更新健康数据
    // await updateHealthData(healthData)
    ElMessage.success('健康数据更新成功')
  } catch (error) {
    ElMessage.error(`健康数据更新失败: ${(error as Error).message}`)
  }
}

const handleShare = async (command: string) => {
  const healthDataString = `身高: ${healthData.height}cm, 体重: ${healthData.weight}kg, 心率: ${healthData.heartRate}次/分, 血压: ${healthData.bloodPressure}`

  switch (command) {
    case 'copy':
      await navigator.clipboard.writeText(healthDataString)
      ElMessage.success('健康数据已复制到剪贴板')
      break
    case 'assistant':
      // 这里应该调用API来分享数据到个人健康助手
      // await shareToAssistant(healthData)
      ElMessage.success('健康数据已分享到个人健康助手')
      break
    case 'friends':
      // 这里应该调用API来分享数据到好友
      // await shareToFriends(healthData)
      ElMessage.success('健康数据已分享到好友')
      break
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
