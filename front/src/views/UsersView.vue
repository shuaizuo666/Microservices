<template>
  <div class="users-view">
    <div class="card">
      <h2>用户列表</h2>
      
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="error" class="error-message">{{ error }}</div>
      <div v-else-if="users.length === 0" class="empty">暂无用户数据</div>
      <div v-else class="users-table-container">
        <table class="users-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>姓名</th>
              <th>邮箱</th>
              <th>角色</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.fullName || '-' }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.role || '普通用户' }}</td>
              <td>
                <span class="status" :class="user.status">{{ user.status || '活跃' }}</span>
              </td>
              <td>{{ formatDate(user.createdAt) }}</td>
              <td>
                <router-link :to="{ name: 'user-detail', params: { id: user.id } }">
                  <button class="view-btn">查看详情</button>
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { userApi } from '../services/api'
import { ref, onMounted } from 'vue'

export default {
  name: 'UsersView',
  setup() {
    const users = ref([])
    const loading = ref(false)
    const error = ref('')
    
    const loadUsers = async () => {
      loading.value = true
      error.value = ''
      
      try {
        const response = await userApi.getAllUsers()
        // 假设后端返回的是数组或包含用户列表的对象
        users.value = Array.isArray(response) ? response : (response.users || [])
      } catch (err) {
        error.value = '获取用户列表失败'
      } finally {
        loading.value = false
      }
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN')
    }
    
    onMounted(() => {
      loadUsers()
    })
    
    return {
      users,
      loading,
      error,
      formatDate
    }
  }
}
</script>

<style scoped>
.users-view {
  overflow-x: auto;
}

.users-table-container {
  overflow-x: auto;
  margin-top: 20px;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.users-table th {
  background-color: #f9fafb;
  font-weight: 600;
  color: #374151;
}

.users-table tr:hover {
  background-color: #f9fafb;
}

.status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
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

.view-btn {
  padding: 6px 12px;
  font-size: 14px;
  background-color: #6366f1;
}

.view-btn:hover {
  background-color: #4f46e5;
}

.loading,
.empty {
  text-align: center;
  padding: 60px;
  color: #6b7280;
}
</style>