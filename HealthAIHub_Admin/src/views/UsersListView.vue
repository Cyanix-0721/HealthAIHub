<template>
  <div class="users-list">
    <div class="actions">
      <el-input v-model="searchQuery" placeholder="搜索用户" class="search-input" @input="handleSearch">
        <template #prefix>
          <el-icon>
            <Search />
          </el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="handleAdd">添加用户</el-button>
    </div>
    <el-table :data="userStore.users" style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="createdAt" label="注册时间">
        <template #default="{ row }">
          {{ formattedDateTime(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="头像" width="100">
        <template #default="scope">
          <el-avatar :size="40" :src="scope.row.avatar" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:current-page="userStore.currentPage" v-model:page-size="userStore.pageSize"
      :total="userStore.totalUsers" @current-change="handlePageChange" @size-change="handleSizeChange"
      layout="total, sizes, prev, pager, next, jumper" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="currentUser" :rules="rules" ref="userForm" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="currentUser.username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="currentUser.email" />
        </el-form-item>
        <el-form-item :label="isAdding ? '密码' : '新密码'" prop="password">
          <el-input v-model="currentUser.password" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import type { User, UserSaveDto } from '@/service/userApi'
import type { FormInstance, FormRules } from 'element-plus'
import { formatDateTime } from '@/utils/dateUtils'

const userStore = useUserStore()

const searchQuery = ref('')
const selectedUsers = ref<User[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const currentUser = ref<UserSaveDto>({
  username: '',
  email: '',
  password: '',
})
const isAdding = ref(false)

const rules = computed<FormRules>(() => {
  const baseRules: FormRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '用户名长度应在 3 到 20 个字符之间', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
      { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
    ]
  }

  if (isAdding.value) {
    baseRules.password = [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
    ]
  } else {
    baseRules.password = [
      { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
    ]
  }

  return baseRules
})

const userForm = ref<FormInstance | null>(null)

const handleSearch = () => {
  userStore.fetchUsers(searchQuery.value)
}

const handleAdd = () => {
  isAdding.value = true
  dialogTitle.value = '添加用户'
  currentUser.value = {
    username: '',
    email: '',
    password: '',
  }
  dialogVisible.value = true
}

const handleEdit = (user: User) => {
  isAdding.value = false
  dialogTitle.value = '编辑用户'
  currentUser.value = {
    id: user.id,
    username: user.username,
    email: user.email,
    password: '',
  }
  dialogVisible.value = true
}

const handleDelete = (user: User) => {
  ElMessageBox.confirm(
    `确定要删除用户 ${user.username} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const success = await userStore.deleteUsers([user.id])
      if (success) {
        ElMessage.success('删除用户成功')
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const handleSave = async () => {
  if (!userForm.value) return

  try {
    await userForm.value.validate()
    const userToSave: UserSaveDto = { ...currentUser.value }

    if (isAdding.value) {
      if (!userToSave.password) {
        ElMessage.error('新用户密码不能为空')
        return
      }
    } else {
      if (!userToSave.password) {
        delete userToSave.password
      }
    }

    const success = await userStore.saveOrUpdateUser(userToSave)
    if (success) {
      ElMessage.success(isAdding.value ? '添加用户成功' : '更新用户成功')
      dialogVisible.value = false
    }
  } catch (error) {
    console.error('保存用户失败:', error)
    ElMessage.error('保存用户失败')
  }
}

const handleSelectionChange = (val: User[]) => {
  selectedUsers.value = val
}

const handlePageChange = (page: number) => {
  userStore.fetchUsers(page.toString())
}

const handleSizeChange = (size: number) => {
  userStore.pageSize = size
  userStore.fetchUsers(searchQuery.value)
}

// 在组件挂载时获取用户数据
userStore.fetchUsers()

const formattedDateTime = computed(() => (dateTimeString: string) => formatDateTime(dateTimeString));
</script>

<style scoped>
.users-list {
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
