import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import AppLayout from '../layouts/AppLayout.vue'
import HomeView from '../views/HomeView.vue'
import { useAuthStore } from '@/stores/auth'
import UserLogin from '@/components/AdminLogin.vue'
import UserRegister from '@/components/AdminRegister.vue'
import ForgotPassword from '@/components/ForgotPassword.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        component: HomeView,
        children: [
          {
            path: '',
            name: 'login',
            component: UserLogin,
          },
          {
            path: 'register',
            name: 'register',
            component: UserRegister,
          },
          {
            path: 'forgot-password',
            name: 'forgotPassword',
            component: ForgotPassword,
          },
        ],
      },
      {
        path: 'about',
        name: 'about',
        component: () => import('../views/AboutView.vue'),
      },
    ],
  },
  {
    path: '/app',
    component: AppLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'user-management',
        name: 'userManagement',
        component: () => import('../views/UserManagementView.vue'),
        children: [
          {
            path: 'users',
            name: 'usersList',
            component: () => import('../views/UsersListView.vue'),
          },
          {
            path: 'admins',
            name: 'adminsList',
            component: () => import('../views/AdminsListView.vue'),
          },
        ],
      },
      {
        path: 'health-data',
        name: 'healthData',
        component: () => import('../views/HealthDataView.vue'),
      },
      // {
      //   path: 'social',
      //   name: 'social',
      //   component: () => import('../views/SocialView.vue'),
      // },
      {
        path: 'logs',
        name: 'logs',
        component: () => import('../views/LogsView.vue'),
        children: [
          {
            path: 'admin-logs',
            name: 'adminLogs',
            component: () => import('../views/AdminLogsView.vue'),
          },
          {
            path: 'user-logs',
            name: 'userLogs',
            component: () => import('../views/UserLogsView.vue'),
          },
        ],
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('../views/ProfileView.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  if (authStore.token && !authStore.currentAdmin) {
    try {
      await authStore.initializeAuth()
    } catch (error) {
      console.error('路由: 有token无存储用户信息: 初始化认证失败:', error)
      next('/')
      return
    }
  }

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/')
  } else if (to.path === '/' && authStore.isLoggedIn) {
    next('/app/user-management')
  } else {
    next()
  }
})

export default router
