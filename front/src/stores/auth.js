import { defineStore } from 'pinia'
import { authApi } from '../services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    loading: false,
    error: null
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  
  actions: {
    // 登录
    async login(credentials) {
      this.loading = true
      this.error = null
      
      try {
        const response = await authApi.login(credentials)
        
        // 保存token和用户信息
        this.token = response.accessToken
        this.user = response.user
        
        // 保存到localStorage
        localStorage.setItem('token', response.accessToken)
        localStorage.setItem('user', JSON.stringify(response.user))
        
        return response
      } catch (err) {
        this.error = err.response?.data?.message || '登录失败，请检查用户名和密码'
        throw err
      } finally {
        this.loading = false
      }
    },
    
    // 注册
    async register(userData) {
      this.loading = true
      this.error = null
      
      try {
        const response = await authApi.register(userData)
        return response
      } catch (err) {
        this.error = err.response?.data?.message || '注册失败'
        throw err
      } finally {
        this.loading = false
      }
    },
    
    // 登出
    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
    
    // 检查认证状态
    checkAuth() {
      const token = localStorage.getItem('token')
      const userStr = localStorage.getItem('user')
      
      if (token && userStr) {
        this.token = token
        try {
          this.user = JSON.parse(userStr)
        } catch (e) {
          this.logout()
        }
      }
    },
    
    // 获取当前用户信息
    async fetchCurrentUser() {
      try {
        const response = await authApi.getCurrentUser()
        this.user = response
        localStorage.setItem('user', JSON.stringify(response))
        return response
      } catch (err) {
        this.logout()
        throw err
      }
    }
  }
})