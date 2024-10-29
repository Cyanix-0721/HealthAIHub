<template>
  <div class="app-layout">
    <aside class="sidebar" :class="{ 'sidebar-collapsed': isCollapsed }">
      <div class="toggle-btn-container">
        <el-button class="toggle-btn" @click="toggleSidebar">
          <el-icon>
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
        </el-button>
      </div>
      <el-menu default-active="user-management" class="el-menu-vertical" :collapse="isCollapsed" @select="handleSelect">
        <el-sub-menu index="user-management">
          <template #title>
            <el-icon>
              <User />
            </el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="user-management/users">用户管理</el-menu-item>
          <el-menu-item index="user-management/admins">管理员管理</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="health-data">
          <el-icon>
            <DataLine />
          </el-icon>
          <template #title>健康数据管理</template>
        </el-menu-item>

        <el-menu-item index="social">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          <template #title>社交管理</template>
        </el-menu-item>

        <el-sub-menu index="logs">
          <template #title>
            <el-icon>
              <Document />
            </el-icon>
            <span>日志管理</span>
          </template>
          <el-menu-item index="logs/admin-logs">管理员操作日志</el-menu-item>
          <el-menu-item index="logs/user-logs">用户操作日志</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="profile">
          <el-icon>
            <UserFilled />
          </el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
      </el-menu>
    </aside>
    <main class="content">
      <header class="app-header">
        <div class="header-right">
          <el-dropdown>
            <el-avatar :size="40" :icon="Avatar" v-if="!currentAdmin?.avatar" />
            <el-avatar :size="40" :src="currentAdmin?.avatar" v-else />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { User, DataLine, ChatDotRound, Document, UserFilled, Fold, Expand, Avatar } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const activeIndex = ref('user-management')
const isCollapsed = ref(false)
const currentAdmin = computed(() => authStore.currentAdmin)

onMounted(async () => {
  if (authStore.token && !authStore.currentAdmin) {
    try {
      await authStore.initializeAuth()
      console.log('AppLayout: 认证初始化成功')
    } catch (error) {
      console.error('AppLayout: 认证初始化失败:', error)
      ElMessage.error('认证失败，请重新登录')
      router.push('/')
    }
  } else if (authStore.currentAdmin) {
    console.log('AppLayout: 管理员信息已从持久化存储中恢复')
  }
})

const handleSelect = (key: string) => {
  activeIndex.value = key
  router.push(`/app/${key}`)
}

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const goToProfile = () => {
  router.push('/app/profile')
}

const logout = async () => {
  try {
    const result = await authStore.logoutAdmin()
    if (result.code === 200) {
      ElMessage.success('退出登录成功')
      router.push('/')
    } else {
      ElMessage.error(result.message || '退出登录失败')
    }
  } catch (error) {
    console.error('退出登录出错:', error)
    ElMessage.error('退出登录失败，请稍后重试')
  }
}
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar {
  width: 250px;
  background-color: transparent;
  padding: 20px;
  border-right: 1px solid var(--color-border);
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  flex-direction: column;
}

.sidebar-collapsed {
  width: 64px;
  padding: 20px 0;
}

.toggle-btn-container {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.sidebar-collapsed .toggle-btn-container {
  justify-content: center;
}

.toggle-btn {
  flex-shrink: 0;
}

.content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.app-header {
  display: flex;
  justify-content: flex-end;
  padding: 10px 20px;
  background-color: transparent;
  border-bottom: 1px solid var(--color-border);
}

.header-right {
  display: flex;
  align-items: center;
}

:deep(.el-menu) {
  border-right: none;
  background-color: transparent;
}

:deep(.el-menu-vertical:not(.el-menu--collapse)) {
  width: 100%;
}

:deep(.el-menu--collapse) {
  width: 64px;
}

:deep(.el-menu-item) {
  background-color: transparent !important;
  border-radius: 8px;
  margin-bottom: 5px;
  transition: all 0.3s ease;
}

/* 移除有子菜单的导航项悬停时的背景色 */
:deep(.el-sub-menu__title:hover) {
  background-color: transparent !important;
}

/* 保持子菜单项的悬停效果 */
:deep(.el-menu-item:hover) {
  background-color: rgba(var(--el-color-primary-rgb), 0.1) !important;
}

/* 确保激活状态的菜单项仍然有明显的标识 */
:deep(.el-menu-item.is-active) {
  background-color: rgba(var(--el-color-primary-rgb), 0.2) !important;
  color: var(--el-color-primary);
}

/* 可选：为有子菜单的导航项添加一些视觉反馈 */
:deep(.el-sub-menu__title:hover) {
  color: var(--el-color-primary);
}

/* 添加以下样式以支持子菜单 */
:deep(.el-sub-menu .el-menu-item) {
  padding-left: 54px !important;
}

:deep(.el-sub-menu__title) {
  padding-left: 20px !important;
}

:deep(.el-menu--collapse .el-sub-menu__title) {
  padding-left: 20px !important;
}
</style>
