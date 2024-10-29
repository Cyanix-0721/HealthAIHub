<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeTab = ref(route.name === 'register' ? 'register' : 'login')

const showTabs = computed(() => {
  return !['forgotPassword'].includes(route.name as string)
})

watch(() => route.name, (newName) => {
  activeTab.value = newName === 'register' ? 'register' : 'login'
})

interface Tab {
  props: {
    name: string;
  };
}

const handleTabClick = (tab: Tab) => {
  if (tab.props.name === 'login') {
    router.push({ name: 'login' })
  } else if (tab.props.name === 'register') {
    router.push({ name: 'register' })
  }
}
</script>

<template>
  <div class="home-view">
    <el-tabs v-if="showTabs" v-model="activeTab" @tab-click="handleTabClick" class="custom-tabs">
      <el-tab-pane label="登录" name="login"></el-tab-pane>
      <el-tab-pane label="注册" name="register"></el-tab-pane>
    </el-tabs>
    <RouterView />
  </div>
</template>

<style scoped>
.home-view {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.custom-tabs {
  width: 100%;
}

:deep(.el-tabs__nav) {
  width: 100%;
  display: flex;
}

:deep(.el-tabs__item) {
  flex: 1;
  text-align: center;
}
</style>
