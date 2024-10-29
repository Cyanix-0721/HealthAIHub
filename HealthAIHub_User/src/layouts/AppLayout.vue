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
      <el-menu default-active="assistant" class="el-menu-vertical" :collapse="isCollapsed" @select="handleSelect">
        <el-menu-item index="assistant">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          <template #title>个人健康助手</template>
        </el-menu-item>
        <el-menu-item index="health-data">
          <el-icon>
            <DataLine />
          </el-icon>
          <template #title>健康数据</template>
        </el-menu-item>
        <el-menu-item index="social">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          <template #title>社交</template>
        </el-menu-item>
        <el-menu-item index="profile">
          <el-icon>
            <User />
          </el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
      </el-menu>
    </aside>
    <main class="content">
      <header class="app-header">
        <div class="header-right">
          <el-dropdown>
            <el-avatar :size="40" :icon="Avatar" v-if="!currentUser?.avatar" />
            <el-avatar :size="40" :src="currentUser?.avatar" v-else />
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
import { User, DataLine, ChatDotRound, Fold, Expand, Avatar } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const activeIndex = ref('assistant')
const isCollapsed = ref(false)
const currentUser = computed(() => authStore.currentUser)

onMounted(async () => {
  if (authStore.token && !authStore.currentUser) {
    try {
      await authStore.initializeAuth()
      console.log('AppLayout: 认证初始化成功')
    } catch (error) {
      console.error('AppLayout: 认证初始化失败:', error)
      ElMessage.error('认证失败，请重新登录')
      router.push('/')
    }
  } else if (authStore.currentUser) {
    console.log('AppLayout: 用户信息已从持久化存储中恢复')
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
    const result = await authStore.logoutUser()
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

:deep(.el-menu-item:hover) {
  background-color: rgba(var(--el-color-primary-rgb), 0.1) !important;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(var(--el-color-primary-rgb), 0.2) !important;
  color: var(--el-color-primary);
}

:deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background-color: var(--el-color-primary);
  border-radius: 0 4px 4px 0;
}
</style>
