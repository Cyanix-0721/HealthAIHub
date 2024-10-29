<template>
  <div class="user-logs">
    <div class="actions">
      <el-input v-model="searchQuery" placeholder="搜索用户日志" class="search-input" @input="handleSearch">
        <template #prefix>
          <el-icon>
            <Search />
          </el-icon>
        </template>
      </el-input>
    </div>
    <el-table :data="filteredLogs" style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="日志ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="operation" label="操作内容" />
      <el-table-column prop="timestamp" label="操作时间" width="180" />
    </el-table>

    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper" :total="totalLogs" @size-change="handleSizeChange"
      @current-change="handleCurrentChange" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

interface UserOperationLog {
  id: number
  userId: number
  operation: string
  timestamp: string
}

const logs = ref<UserOperationLog[]>([])
const searchQuery = ref('')
const selectedLogs = ref<UserOperationLog[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalLogs = ref(0)

const filteredLogs = computed(() => {
  return logs.value.filter(log =>
    log.operation.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    log.userId.toString().includes(searchQuery.value)
  )
})

const handleSearch = () => {
  // 实现搜索逻辑，可能需要重新获取数据
}

const handleSelectionChange = (val: UserOperationLog[]) => {
  selectedLogs.value = val
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  // 重新获取数据
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  // 重新获取数据
}

// 在组件挂载时获取日志数据
// 这里使用模拟数据，实际应用中应该从API获取
logs.value = [
  { id: 1, userId: 1, operation: '登录系统', timestamp: '2023-05-01 10:00:00' },
  { id: 2, userId: 2, operation: '更新个人信息', timestamp: '2023-05-01 11:30:00' },
]
totalLogs.value = logs.value.length
</script>

<style scoped>
.user-logs {
  width: 100%;
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}
</style>
