# ============================================
# 第一阶段：构建阶段（Build Stage）
# 使用 Maven 官方镜像作为基础镜像，标签 3.9-eclipse-temurin-17 表示：
# - Maven 版本 3.9
# - 基于 Eclipse Temurin 的 JDK 17（Eclipse Temurin 是 Adoptium 提供的开源 JDK）
# AS build 表示给这个阶段起个名字叫 "build"，方便后面引用
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS build

# 设置工作目录为 /app，后续的操作都会在这个目录下进行
WORKDIR /app

# 将当前目录（项目根目录）的所有文件复制到容器的 /app 目录下
# 这一步会把 pom.xml、源代码等都复制进去
COPY . .

# 执行 Maven 打包命令：
# - package: 打包项目
# - -DskipTests: 跳过测试，加快构建速度
# - -B: 批处理模式，减少日志输出
# - -pl consumer: 只构建 consumer 模块（假设是多模块项目）
# - -am: 同时构建 consumer 模块依赖的其他模块（also-make）
# --mount=type=cache: 将 Maven 本地仓库挂载为持久化缓存，避免每次构建都重新下载依赖
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -B -pl consumer -am

# ============================================
# 第二阶段：运行阶段（Runtime Stage）
# 使用更轻量的 JRE 镜像，而不是完整的 JDK
# eclipse-temurin:17-jre 只包含 Java 运行时环境，体积更小，更适合生产环境
# 这种多阶段构建的好处是：最终镜像不会包含 Maven 和 JDK 的编译工具，更加精简安全
# ============================================
FROM eclipse-temurin:17-jre

# 设置工作目录为 /app
WORKDIR /app

# 从第一阶段的 build 镜像中复制打包好的 jar 文件
# --from=build 表示从名为 build 的阶段复制文件
# /app/consumer/target/consumer*.jar 是 Maven 打包生成的 jar 文件路径
# 复制到当前阶段的 app.jar
COPY --from=build /app/consumer/target/consumer*.jar app.jar

# 设置容器启动时执行的命令
# 使用 exec 格式的 ENTRYPOINT（JSON 数组格式），这是推荐的方式
# 启动 Java 应用，运行 app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
