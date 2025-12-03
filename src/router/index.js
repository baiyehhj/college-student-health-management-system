import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '@/views/Login.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  // 学生端路由
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页', requiresAuth: true, role: 'STUDENT' },
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据面板', requiresAuth: true, role: 'STUDENT' }
      },
      {
        path: '/diet-record',
        name: 'DietRecord',
        component: () => import('@/views/DietRecord.vue'),
        meta: { title: '饮食记录', requiresAuth: true, role: 'STUDENT' }
      },
      {
        path: '/exercise-record',
        name: 'ExerciseRecord',
        component: () => import('@/views/ExerciseRecord.vue'),
        meta: { title: '运动记录', requiresAuth: true, role: 'STUDENT' }
      },
      {
        path: '/sleep-record',
        name: 'SleepRecord',
        component: () => import('@/views/SleepRecord.vue'),
        meta: { title: '作息记录', requiresAuth: true, role: 'STUDENT' }
      },
      {
        path: '/mood-record',
        name: 'MoodRecord',
        component: () => import('@/views/MoodRecord.vue'),
        meta: { title: '情绪记录', requiresAuth: true, role: 'STUDENT' }
      },
      {
        path: '/health-exam',
        name: 'HealthExam',
        component: () => import('@/views/HealthExam.vue'),
        meta: { title: '体检报告', requiresAuth: true, role: 'STUDENT' }
      },
      {
        path: '/data-analysis',
        name: 'DataAnalysis',
        component: () => import('@/views/DataAnalysis.vue'),
        meta: { title: '数据分析', requiresAuth: true, role: 'STUDENT' }
      },
      {
        path: '/ai-report',
        name: 'AIReport',
        component: () => import('@/views/AIReport.vue'),
        meta: { title: 'AI健康报告', requiresAuth: true, role: 'STUDENT' }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true, role: 'STUDENT' }
      },
      {
        path: '/notification',
        name: 'Notification',
        component: () => import('@/views/Notification.vue'),
        meta: { title: '系统通知', requiresAuth: true, role: 'STUDENT' }
      }
    ]
  },
  // 管理员端路由
  {
    path: '/admin',
    name: 'AdminHome',
    component: () => import('@/views/admin/AdminHome.vue'),
    meta: { title: '管理后台', requiresAuth: true, role: 'ADMIN' },
    redirect: '/admin/dashboard',
    children: [
      {
        path: '/admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '数据统计', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: '/admin/students',
        name: 'StudentManagement',
        component: () => import('@/views/admin/StudentManagement.vue'),
        meta: { title: '学生管理', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: '/admin/alerts',
        name: 'HealthAlerts',
        component: () => import('@/views/admin/HealthAlerts.vue'),
        meta: { title: '健康预警', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: '/admin/export',
        name: 'DataExport',
        component: () => import('@/views/admin/DataExport.vue'),
        meta: { title: '数据导出', requiresAuth: true, role: 'ADMIN' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 健康管理系统` : '健康管理系统'
  
  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
      return
    }
    
    // 检查角色权限
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const requiredRole = to.meta.role
    
    if (requiredRole && userInfo.role !== requiredRole) {
      // 角色不匹配，重定向到对应的首页
      if (userInfo.role === 'ADMIN') {
        next('/admin/dashboard')
      } else {
        next('/dashboard')
      }
      return
    }
  }
  
  next()
})

export default router
