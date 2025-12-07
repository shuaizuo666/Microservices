<template>
  <div class="home-view">
    <div class="card">
      <h1>欢迎使用认证服务</h1>
      <p v-if="!isLoggedIn">请登录或注册以使用完整功能。</p>
      <p v-else>欢迎回来，{{ user?.fullName || user?.username }}！</p>
      
      <div v-if="!isLoggedIn" class="action-buttons">
        <router-link to="/login">
          <button>去登录</button>
        </router-link>
        <router-link to="/register">
          <button style="background-color: #10b981;">去注册</button>
        </router-link>
      </div>
      
      <div v-else class="user-info">
        <router-link to="/profile">
          <button>查看个人资料</button>
        </router-link>
        <router-link to="/users">
          <button style="background-color: #6366f1;">用户管理</button>
        </router-link>
      </div>
    </div>
    
    <div class="card features">
      <h2>系统功能</h2>
      <div class="features-list">
        <div class="feature-item">
          <h3>用户认证</h3>
          <p>安全的用户登录和注册功能，基于JWT的身份验证。</p>
        </div>
        <div class="feature-item">
          <h3>用户管理</h3>
          <p>查看和管理用户信息，支持按ID查询。</p>
        </div>
        <div class="feature-item">
          <h3>个人资料</h3>
          <p>查看当前登录用户的详细信息。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth'
import { computed } from 'vue'

export default {
  name: 'HomeView',
  setup() {
    const authStore = useAuthStore()
    
    const isLoggedIn = computed(() => authStore.isLoggedIn)
    const user = computed(() => authStore.user)
    
    return {
      isLoggedIn,
      user
    }
  }
}
</script>

<style scoped>
.home-view {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.action-buttons,
.user-info {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.features-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.feature-item {
  padding: 20px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.feature-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feature-item h3 {
  color: #3b82f6;
  margin-bottom: 8px;
}
</style>