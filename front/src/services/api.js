import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage中获取token
    const token = localStorage.getItem('token')
    // 如果token存在，则添加到请求头中
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    // 处理401错误（未授权）
    if (error.response && error.response.status === 401) {
      // 清除token
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // 跳转到登录页面
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 导出api实例
export default api

// 认证相关API
export const authApi = {
  // 登录
  login: (credentials) => api.post('auth/login', credentials),
  // 注册
  register: (userData) => api.post('auth/register', userData),
  // 获取当前用户信息
  getCurrentUser: () => api.get('users/me')
}

// 用户相关API
export const userApi = {
  // 根据ID获取用户信息
  getUserById: (id) => api.get(`users/${id}`),
  // 获取所有用户列表
  getAllUsers: () => api.get('users'),
  // 更新用户信息
  updateUser: (id, userData) => api.put(`users/${id}`, userData),
  // 删除用户
  deleteUser: (id) => api.delete(`users/${id}`),
  // 切换用户状态
  toggleUserStatus: (id) => api.patch(`users/${id}/status`)
}