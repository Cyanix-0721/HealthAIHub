<template>
  <div class="admins-list">
    <div class="actions">
      <el-input v-model="searchQuery" placeholder="搜索管理员" class="search-input" @input="handleSearch">
        <template #prefix>
          <el-icon>
            <Search />
          </el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="handleAdd">添加管理员</el-button>
    </div>
    <el-table :data="adminStore.admins" style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="管理员用户名" />
      <el-table-column prop="email" label="管理员邮箱" />
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

    <el-pagination v-model:current-page="adminStore.currentPage" v-model:page-size="adminStore.pageSize"
      :total="adminStore.totalAdmins" @current-change="handlePageChange" @size-change="handleSizeChange"
      layout="total, sizes, prev, pager, next, jumper" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="currentAdmin" :rules="rules" ref="adminForm" label-width="100px">
        <el-form-item label="管理员用户名" prop="username">
          <el-input v-model="currentAdmin.username" />
        </el-form-item>
        <el-form-item label="管理员邮箱" prop="email">
          <el-input v-model="currentAdmin.email" />
        </el-form-item>
        <el-form-item :label="isAdding ? '密码' : '新密码'" prop="password">
          <el-input v-model="currentAdmin.password" type="password" />
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
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useAdminStore } from '@/stores/admin'
import type { Admin, AdminSaveDto } from '@/service/adminApi'
import type { FormInstance, FormRules } from 'element-plus'
import { formatDateTime } from '@/utils/dateUtils'

const adminStore = useAdminStore()

const searchQuery = ref('')
const selectedAdmins = ref<Admin[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const currentAdmin = ref<AdminSaveDto>({
  username: '',
  email: '',
  password: '',
})
const isAdding = ref(false)

const rules = computed<FormRules>(() => {
  const baseRules: FormRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 10, message: '用户名长度应在 3 到 10 个字符之间', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
      { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
    ]
  }

  if (isAdding.value) {
    baseRules.password = [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度应在 6 到 20 个字符', trigger: 'blur' }
    ]
  } else {
    baseRules.password = [
      { min: 6, max: 20, message: '密码长度应在 6 到 20 个字符', trigger: 'blur' }
    ]
  }

  return baseRules
})

const adminForm = ref<FormInstance | null>(null)

const handleSearch = () => {
  adminStore.fetchAdmins(searchQuery.value)
}

const handleAdd = () => {
  dialogTitle.value = '添加管理员'
  currentAdmin.value = {
    username: '',
    email: '',
    password: '',
  }
  isAdding.value = true
  dialogVisible.value = true
}

const handleEdit = (admin: Admin) => {
  dialogTitle.value = '编辑管理员'
  currentAdmin.value = {
    id: admin.id,
    username: admin.username,
    email: admin.email,
    password: '',
  }
  isAdding.value = false
  dialogVisible.value = true
}

const handleDelete = (admin: Admin) => {
  ElMessageBox.confirm(
    `确定要删除管理员 ${admin.username} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    adminStore.deleteAdmins([admin.id])
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const handleSave = async () => {
  if (!adminForm.value) return

  try {
    await adminForm.value.validate()
    const adminToSave: AdminSaveDto = { ...currentAdmin.value }

    if (isAdding.value) {
      if (!adminToSave.password) {
        ElMessage.error('新管理员密码不能为空')
        return
      }
    } else {
      // 编辑管理员时，如果密码为空，则从请求中删除密码字段
      if (!adminToSave.password) {
        delete adminToSave.password
      }
      // 确保在编辑时包含 id
      adminToSave.id = currentAdmin.value.id
    }

    const success = await adminStore.saveOrUpdateAdmin(adminToSave)
    if (success) {
      ElMessage.success(isAdding.value ? '添加管理员成功' : '更新管理员成功')
      dialogVisible.value = false
    }
  } catch (error) {
    console.error('保存管理员失败:', error)
    ElMessage.error('保存管理员失败')
  }
}

const handleSelectionChange = (val: Admin[]) => {
  selectedAdmins.value = val
}

const handlePageChange = (page: number) => {
  adminStore.currentPage = page
  adminStore.fetchAdmins()
}

const handleSizeChange = (size: number) => {
  adminStore.pageSize = size
  adminStore.fetchAdmins()
}

const formattedDateTime = computed(() => (dateTimeString: string) => formatDateTime(dateTimeString))

onMounted(() => {
  adminStore.fetchAdmins()
})
</script>

<style scoped>
.admins-list {
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
