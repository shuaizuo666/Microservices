<template>
  <div class="register-view">
    <div class="card register-card">
      <h2>用户注册</h2>
      
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">用户名 *</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            placeholder="请输入用户名（3-50个字符）"
            required
            minlength="3"
            maxlength="50"
            :disabled="loading"
          />
        </div>
        <div class="form-group">
          <label for="email">邮箱 *</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            placeholder="请输入邮箱地址"
            required
            :disabled="loading"
          />
        </div>
        <div class="form-group">
          <label for="password">密码 *</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6个字符）"
            required
            minlength="6"
            :disabled="loading"
            autocomplete="new-password"
          />
        </div>
        <div class="form-group">
          <label for="fullName">姓名</label>
          <input
            id="fullName"
            v-model="form.fullName"
            type="text"
            placeholder="请输入姓名"
            :disabled="loading"
          />
        </div>
        <div class="form-group">
          <label for="phone">手机号码</label>
          <input
            id="phone"
            v-model="form.phone"
            type="tel"
            placeholder="请输入手机号码"
            :disabled="loading"
          />
        </div>
        <div v-if="error" class="error-message">{{ error }}</div>
        <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
        <button type="submit" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      
      <p class="login-link">
        已有账号？<router-link to="/login">立即登录</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'RegisterView',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    
    const form = ref({
      username: '',
      email: '',
      password: '',
      fullName: '',
      phone: ''
    })
    
    const loading = ref(false)
    const error = ref('')
    const successMessage = ref('')
    
    const handleRegister = async () => {
      error.value = ''
      successMessage.value = ''
      loading.value = true
      
      try {
        await authStore.register(form.value)
        successMessage.value = '注册成功！即将跳转到登录页面...'
        
        // 2秒后跳转到登录页面
        setTimeout(() => {
          router.push('/login')
        }, 2000)
      } catch (err) {
        error.value = authStore.error || '注册失败，请重试'
      } finally {
        loading.value = false
      }
    }
    
    return {
      form,
      loading,
      error,
      successMessage,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.register-card {
  width: 100%;
  max-width: 500px;
}

.login-link {
  margin-top: 20px;
  text-align: center;
}

.login-link a {
  color: #3b82f6;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>