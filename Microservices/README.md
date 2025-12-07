# 智慧城市身份验证服务

## 项目简介

这是一个基于Spring Boot的微服务，提供用户管理和身份验证功能，是智慧城市生态系统的核心组件之一。该服务支持用户注册、登录（返回JWT令牌）以及用户信息查询等功能。

## 技术栈

- **后端框架**：Spring Boot 3.2.0
- **安全框架**：Spring Security
- **认证方式**：JWT (JSON Web Token)
- **数据库**：MySQL
- **ORM框架**：Spring Data JPA
- **容器化**：Docker
- **构建工具**：Maven

## 功能特性

- 用户注册
- 用户登录（返回JWT令牌）
- 获取当前用户信息
- 获取指定用户信息
- 获取所有用户列表
- JWT令牌验证
- 密码加密存储
- 日志记录

## 数据库配置

服务使用MySQL数据库，连接信息在`application.yml`中配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_service?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: password
```

## API端点

### 认证相关

- **POST /api/auth/register** - 用户注册
  - 请求体：
    ```json
    {
      "username": "user1",
      "email": "user1@example.com",
      "password": "password123",
      "fullName": "张三",
      "phone": "13800138000"
    }
    ```

- **POST /api/auth/login** - 用户登录
  - 请求体：
    ```json
    {
      "username": "user1",
      "password": "password123"
    }
    ```
  - 响应：
    ```json
    {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
      "tokenType": "Bearer",
      "expiresIn": 86400000,
      "user": {
        "id": 1,
        "username": "user1",
        "email": "user1@example.com",
        "fullName": "张三",
        "phone": "13800138000",
        "role": "USER",
        "status": "ACTIVE",
        "createdAt": "2023-01-01T10:00:00",
        "lastLogin": "2023-01-01T10:00:00",
        "avatarUrl": null
      }
    }
    ```

### 用户相关

- **GET /api/users/me** - 获取当前用户信息
  - 需要在请求头中添加：`Authorization: Bearer <token>`

- **GET /api/users/{id}** - 获取指定用户信息
  - 需要在请求头中添加：`Authorization: Bearer <token>`

- **GET /api/users** - 获取所有用户列表
  - 需要在请求头中添加：`Authorization: Bearer <token>`

## 安装与运行

### 本地开发环境

1. 确保已安装JDK 17和Maven
2. 确保MySQL数据库已运行
3. 修改`application.yml`中的数据库连接信息
4. 运行应用：
   ```bash
   mvn spring-boot:run
   ```

### Docker容器化部署

1. 构建Docker镜像：
   ```bash
   docker build -t auth-service .
   ```

2. 运行容器：
   ```bash
   docker run -d -p 8081:8081 --name auth-service --link mysql:mysql auth-service
   ```

## 安全注意事项

- 在生产环境中，务必修改`application.yml`中的JWT密钥
- 确保数据库密码安全存储
- 考虑使用HTTPS协议
- 定期更新依赖以修复安全漏洞

## 许可证

本项目仅供学习和演示使用。