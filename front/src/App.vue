<template>
  <div class="app">
    <nav class="nav">
  <div class="nav-brand">
    <h1>认证服务</h1>
      </div>
        <div class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link v-if="!isLoggedIn" to="/login">登录</router-link>
          <router-link v-if="!isLoggedIn" to="/register">注册</router-link>
          <router-link v-if="isLoggedIn" to="/profile">个人资料</router-link>
          <router-link v-if="isLoggedIn" to="/users">用户列表</router-link>
          <router-link v-if="isAdmin" to="/admin/users">用户管理</router-link>
          <button v-if="isLoggedIn" @click="logout" class="logout-btn">退出登录</button>
      </div>
    </nav>
    
    <div class="container">
      <router-view />
    </div>
  </div>
</template>

<script>
import { useAuthStore } from './stores/auth'
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    
    const isLoggedIn = computed(() => authStore.isLoggedIn)
    const isAdmin = computed(() => authStore.user?.role === 'ADMIN')
    
    const logout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    onMounted(() => {
      authStore.checkAuth()
    })
    
    return {
      isLoggedIn,
      isAdmin,
      logout
    }
  }
}
</script>

<style>
.logout-btn {
  padding: 6px 12px;
  background-color: #ef4444;
}

.logout-btn:hover {
  background-color: #dc2626;
}
</style>