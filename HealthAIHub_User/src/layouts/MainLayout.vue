<template>
  <div class="main-layout">
    <aside class="sidebar">
      <div class="header-wrapper">
        <Icon icon="healthicons:health-outline" alt="HealthAIHub logo" class="logo" width="120" height="120"
          color="lightgreen" />
        <HelloWorld msg1="健康伙伴" msg2="HealthAIHub" />
      </div>
      <nav class="nav">
        <RouterLink to="/" class="nav-link" active-class="active-link">主页</RouterLink>
        <RouterLink to="/about" class="nav-link" active-class="active-link">关于我们</RouterLink>
      </nav>
    </aside>
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from '../components/HelloWorld.vue'
import { Icon } from '@iconify/vue'
</script>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar {
  width: 50%;
  padding: 20px;
  background-color: var(--color-background);
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 水平居中 */
  justify-content: center;
  /* 垂直居中 */
  position: relative;
  /* 允许调整整体位置 */
  left: 20px;
  /* 向右移动20px，根据需要调整 */
}

.header-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  /* 水平居中 */
  gap: 20px;
  /* LOGO 和 HelloWorld 之间的间距 */
  width: 100%;
}

.nav {
  display: flex;
  gap: 20px;
  /* 导航链接之间的间距 */
  margin-top: 40px;
  /* 与 header-wrapper 分开 */
}

.nav-link {
  padding: 10px 15px;
  text-decoration: none;
  color: var(--color-text);
  border-radius: 4px;
  transition: color 0.3s;
}

/* 覆盖全局 a:hover 的背景颜色 */
.nav-link:hover {
  color: var(--color-primary);
  /* 悬停时字体变为蓝色 */
  background-color: transparent;
  /* 移除悬停时的背景颜色 */
}

.active-link {
  color: var(--color-primary);
  /* 激活时字体变为蓝色 */
  background-color: transparent;
  /* 移除激活时的背景颜色 */
}

.content {
  width: 50%;
  padding: 20px;
  overflow-y: auto;
  display: block;
  /* 移除 flex 布局 */
}
</style>
