<template>
  <div class="not-found-view">
    <div class="card">
      <h1 class="error-code">404</h1>
      <h2>页面未找到</h2>
      <p class="error-message">您访问的页面不存在或已被移除。</p>
      <router-link to="/">
        <button class="home-btn">返回首页</button>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.not-found-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.card {
  text-align: center;
  padding: 60px 40px;
  max-width: 500px;
  width: 100%;
}

.error-code {
  font-size: 80px;
  font-weight: bold;
  color: #3b82f6;
  margin-bottom: 20px;
  line-height: 1;
}

h2 {
  font-size: 28px;
  margin-bottom: 16px;
  color: #1f2937;
}

.error-message {
  font-size: 16px;
  color: #6b7280;
  margin-bottom: 30px;
  line-height: 1.5;
}

.home-btn {
  padding: 12px 24px;
  font-size: 16px;
  background-color: #3b82f6;
}

.home-btn:hover {
  background-color: #2563eb;
}
</style>