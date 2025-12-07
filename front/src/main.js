import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import App from './App.vue'
import routes from './router'
import './services/api'

const app = createApp(App)
const router = createRouter({
  history: createWebHistory(),
  routes
})

const pinia = createPinia()

// 全局路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 需要认证的路由
    if (!token) {
      next({ path: '/login' })
    } else if (to.matched.some(record => record.meta.requiresAdmin)) {
      // 需要管理员权限的路由
      if (user.role === 'ADMIN') {
        next()
      } else {
        next({ path: '/' })
      }
    } else {
      next()
    }
  } else {
    // 不需要认证的路由
    next()
  }
})

app.use(router)
app.use(pinia)
app.mount('#app')