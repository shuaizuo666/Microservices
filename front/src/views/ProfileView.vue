<template>
  <div class="profile-view">
    <div class="card">
      <h2>个人资料</h2>
      
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="error" class="error-message">{{ error }}</div>
      <div v-else-if="user" class="user-details">
        <div class="profile-header">
          <div class="avatar">
            <img v-if="user.avatarUrl" :src="user.avatarUrl" alt="头像" />
            <div v-else class="avatar-placeholder">
              {{ getInitials(user.fullName || user.username) }}
            </div>
          </div>
          <div class="user-basic-info">
            <h3>{{ user.fullName || user.username }}</h3>
            <p class="username">@{{ user.username }}</p>
          </div>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <strong>用户ID：</strong>
            <span>{{ user.id }}</span>
          </div>
          <div class="info-item">
            <strong>邮箱：</strong>
            <span>{{ user.email }}</span>
          </div>
          <div class="info-item">
            <strong>手机号码：</strong>
            <span>{{ user.phone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <strong>角色：</strong>
            <span>{{ user.role || '普通用户' }}</span>
          </div>
          <div class="info-item">
            <strong>状态：</strong>
            <span class="status" :class="user.status">{{ user.status || '活跃' }}</span>
          </div>
          <div class="info-item">
            <strong>创建时间：</strong>
            <span>{{ formatDate(user.createdAt) }}</span>
          </div>
          <div class="info-item">
            <strong>最后登录：</strong>
            <span>{{ user.lastLogin ? formatDate(user.lastLogin) : '从未登录' }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth'
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'ProfileView',
  setup() {
    const authStore = useAuthStore()
    const loading = ref(false)
    const error = ref('')
    
    const user = computed(() => authStore.user)
    
    const loadUserInfo = async () => {
      loading.value = true
      error.value = ''
      
      try {
        await authStore.fetchCurrentUser()
      } catch (err) {
        error.value = '获取用户信息失败'
      } finally {
        loading.value = false
      }
    }
    
    const getInitials = (name) => {
      if (!name) return '?'
      const parts = name.trim().split(' ')
      if (parts.length > 1) {
        return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
      }
      return name.charAt(0).toUpperCase()
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }
    
    onMounted(() => {
      loadUserInfo()
    })
    
    return {
      user,
      loading,
      error,
      getInitials,
      formatDate
    }
  }
}
</script>

<style scoped>
.profile-view {
  display: flex;
  justify-content: center;
}

.user-details {
  width: 100%;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  background-color: #f3f4f6;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #3b82f6;
  color: white;
  font-size: 36px;
  font-weight: bold;
}

.user-basic-info h3 {
  margin-bottom: 5px;
}

.username {
  color: #6b7280;
  font-size: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item strong {
  color: #1f2937;
  font-size: 14px;
}

.info-item span {
  color: #4b5563;
  font-size: 16px;
}

.status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
}

.status.active {
  background-color: #d1fae5;
  color: #065f46;
}

.status.inactive {
  background-color: #fee2e2;
  color: #991b1b;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #6b7280;
}
</style>