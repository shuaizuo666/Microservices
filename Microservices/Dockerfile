# 多阶段构建：第一阶段用于构建项目
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 先复制依赖文件并下载依赖（利用Docker缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行时镜像
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 添加非root用户
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 复制构建好的JAR文件
COPY --from=builder /app/target/auth-service-1.0-SNAPSHOT.jar app.jar

# 复制数据库初始化脚本
COPY --from=builder /app/src/main/resources/init_db.sql init_db.sql

# 更改文件所有权
RUN chown appuser:appgroup app.jar init_db.sql

# 切换到非root用户
USER appuser

# 暴露应用端口
EXPOSE 8081

# 设置环境变量
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -Dspring.profiles.active=prod"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]