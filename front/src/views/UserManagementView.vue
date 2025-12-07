
<template>
  <div class="user-management-view">
    <div class="card">
      <div class="header">
        <h2>用户管理</h2>
        <button @click="showCreateModal = true" class="create-btn">
          创建用户
        </button>
      </div>
      
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="error" class="error-message">{{ error }}</div>
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
              <td>
                <select v-model="user.role" @change="updateUserRole(user)" 
                        :disabled="user.id === currentUser.id">
                  <option value="USER">普通用户</option>
                  <option value="ADMIN">管理员</option>
                </select>
              </td>
              <td>
                <span class="status" :class="user.status.toLowerCase()">
                  {{ user.status === 'ACTIVE' ? '活跃' : '禁用' }}
                </span>
              </td>
              <td>{{ formatDate(user.createdAt) }}</td>
              <td>
                <div class="action-buttons">
                  <button @click="editUser(user)" class="edit-btn">编辑</button>
                  <button @click="toggleUserStatus(user)" 
                          :class="user.status === 'ACTIVE' ? 'disable-btn' : 'enable-btn'"
                          :disabled="user.id === currentUser.id">
                    {{ user.status === 'ACTIVE' ? '禁用' : '启用' }}
                  </button>
                  <button @click="deleteUser(user)" 
                          class="delete-btn"
                          :disabled="user.id === currentUser.id">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 创建/编辑用户模态框 -->
    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h3>{{ showEditModal ? '编辑用户' : '创建用户' }}</h3>
        <form @submit.prevent="showEditModal ? updateUser() : createUser()">
          <div class="form-group">
            <label for="username">用户名 *</label>
            <input
              id="username"
              v-model="userForm.username"
              type="text"
              placeholder="请输入用户名"
              required
              :disabled="showEditModal"
            />
          </div>
          <div class="form-group">
            <label for="email">邮箱 *</label>
            <input
              id="email"
              v-model="userForm.email"
              type="email"
              placeholder="请输入邮箱"
              required
            />
          </div>
          <div v-if="!showEditModal" class="form-group">
            <label for="password">密码 *</label>
            <input
              id="password"
              v-model="userForm.password"
              type="password"
              placeholder="请输入密码"
              required
            />
          </div>
          <div class="form-group">
            <label for="fullName">姓名</label>
            <input
              id="fullName"
              v-model="userForm.fullName"
              type="text"
              placeholder="请输入姓名"
            />
          </div>
          <div class="form-group">
            <label for="phone">手机号码</label>
            <input
              id="phone"
              v-model="userForm.phone"
              type="tel"
              placeholder="请输入手机号码"
            />
          </div>
          <div class="form-group">
            <label for="role">角色</label>
            <select id="role" v-model="userForm.role">
              <option value="USER">普通用户</option>
              <option value="ADMIN">管理员</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="cancel-btn">取消</button>
            <button type="submit" :disabled="loading">
              {{ loading ? '提交中...' : (showEditModal ? '更新' : '创建') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { userApi } from '../services/api'
import { useAuthStore } from '../stores/auth'
import { ref, onMounted, computed } from 'vue'

export default {
  name: 'UserManagementView',
  setup() {
    const authStore = useAuthStore()
    const users = ref([])
    const loading = ref(false)
    const error = ref('')
    const showCreateModal = ref(false)
    const showEditModal = ref(false)
    const editingUser = ref(null)

    const currentUser = computed(() => authStore.user)

    const userForm = ref({
      username: '',
      email: '',
      password: '',
      fullName: '',
      phone: '',
      role: 'USER'
    })

    const loadUsers = async () => {
      loading.value = true
      error.value = ''
      
      try {
        const response = await userApi.getAllUsers()
        users.value = Array.isArray(response) ? response : (response.users || [])
      } catch (err) {
        error.value = '获取用户列表失败'
      } finally {
        loading.value = false
      }
    }

    const createUser = async () => {
      try {
        loading.value = true
        await authStore.register(userForm.value)
        closeModal()
        loadUsers()
      } catch (err) {
        error.value = authStore.error || '创建用户失败'
      } finally {
        loading.value = false
      }
    }

    const editUser = (user) => {
      editingUser.value = user
      userForm.value = {
        username: user.username,
        email: user.email,
        fullName: user.fullName || '',
        phone: user.phone || '',
        role: user.role || 'USER'
      }
      showEditModal.value = true
    }

    const updateUser = async () => {
      try {
        loading.value = true
        await userApi.updateUser(editingUser.value.id, userForm.value)
        closeModal()
        loadUsers()
      } catch (err) {
        error.value = '更新用户失败'
      } finally {
        loading.value = false
      }
    }

    const updateUserRole = async (user) => {
      try {
        await userApi.updateUser(user.id, { role: user.role })
        loadUsers()
      } catch (err) {
        error.value = '更新用户角色失败'
        loadUsers() // 重新加载以恢复原状态
      }
    }

    const toggleUserStatus = async (user) => {
      if (user.id === currentUser.value.id) {
        error.value = '不能操作自己的账号'
        return
      }

      if (!confirm(`确定要${user.status === 'ACTIVE' ? '禁用' : '启用'}用户 ${user.username} 吗？`)) {
        return
      }

      try {
        await userApi.toggleUserStatus(user.id)
        loadUsers()
      } catch (err) {
        error.value = '操作失败'
      }
    }

    const deleteUser = async (user) => {
      if (user.id === currentUser.value.id) {
        error.value = '不能删除自己的账号'
        return
      }

      if (!confirm(`确定要删除用户 ${user.username} 吗？此操作不可恢复。`)) {
        return
      }

      try {
        await userApi.deleteUser(user.id)
        loadUsers()
      } catch (err) {
        error.value = '删除用户失败'
      }
    }

    const closeModal = () => {
      showCreateModal.value = false
      showEditModal.value = false
      editingUser.value = null
      userForm.value = {
        username: '',
        email: '',
        password: '',
        fullName: '',
        phone: '',
        role: 'USER'
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
      showCreateModal,
      showEditModal,
      userForm,
      currentUser,
      loadUsers,
      createUser,
      editUser,
      updateUser,
      updateUserRole,
      toggleUserStatus,
      deleteUser,
      closeModal,
      formatDate
    }
  }
}
</script>

<style scoped>
.user-management-view {
  overflow-x: auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.create-btn {
  background-color: #10b981;
}

.create-btn:hover {
  background-color: #059669;
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
  padding: 4px 8px;
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

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.edit-btn {
  background-color: #3b82f6;
  padding: 6px 12px;
  font-size: 14px;
}

.edit-btn:hover {
  background-color: #2563eb;
}

.disable-btn {
  background-color: #f59e0b;
  padding: 6px 12px;
  font-size: 14px;
}

.disable-btn:hover {
  background-color: #d97706;
}

.enable-btn {
  background-color: #10b981;
  padding: 6px 12px;
  font-size: 14px;
}

.enable-btn:hover {
  background-color: #059669;
}

.delete-btn {
  background-color: #ef4444;
  padding: 6px 12px;
  font-size: 14px;
}

.delete-btn:hover {
  background-color: #dc2626;
}

.delete-btn:disabled,
.disable-btn:disabled,
.enable-btn:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
}

select {
  padding: 6px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  margin-bottom: 20px;
  text-align: center;
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.cancel-btn {
  background-color: #6b7280;
  flex: 1;
}

.cancel-btn:hover {
  background-color: #4b5563;
}

.modal-actions button[type="submit"] {
  flex: 2;
}

.loading,
.empty {
  text-align: center;
  padding: 60px;
  color: #6b7280;
}
</style>
