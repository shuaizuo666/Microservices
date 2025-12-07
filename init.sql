-- 创建数据库用户并设置权限
CREATE USER IF NOT EXISTS 'appuser'@'%' IDENTIFIED BY 'secure_app_password';
GRANT ALL PRIVILEGES ON Microservices.* TO 'appuser'@'%';
FLUSH PRIVILEGES;

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS Microservices;

-- 使用数据库
USE Microservices;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    avatar_url VARCHAR(255)
);

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

-- 插入管理员用户
INSERT INTO users (username, email, password, full_name, role) 
VALUES ('admin', 'admin@smartcity.com', '$2a$10$wVgWI.j7Ms/ztOdFTrfPwOvUvfrepLmt/Ji6A8tISNUsrXc.pk4cO', '系统管理员', 'ADMIN') 
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;