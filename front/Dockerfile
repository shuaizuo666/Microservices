# 多阶段构建：第一阶段用于构建前端项目
FROM node:18-alpine AS builder

# 设置工作目录
WORKDIR /app

# 先复制package文件并安装依赖（利用Docker缓存层）
COPY package*.json ./
RUN npm ci

# 复制项目文件并构建
COPY . .
RUN npm run build

# 第二阶段：运行时镜像，使用Nginx
FROM nginx:alpine

# 复制构建好的静态文件到Nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制自定义Nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 设置非root用户运行Nginx（增强安全性）
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    sed -i 's/user nginx;/user nginx;\n    worker_processes auto;/g' /etc/nginx/nginx.conf

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"]