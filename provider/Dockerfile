# ============================================
# 第一阶段：构建阶段（Build Stage）
# 使用 Maven 官方镜像作为基础镜像，标签 3.9-eclipse-temurin-17 表示：
# - Maven 版本 3.9
# - 基于 Eclipse Temurin 的 JDK 17（这是 Adoptium 提供的开源 JDK）
# AS build 给这个阶段起个名字叫 "build"，方便后面引用
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS build

# 设置工作目录为 /app，后续命令都会在这个目录下执行
WORKDIR /app

# 将当前目录（项目代码）复制到容器的 /app 目录下
# 这包括 pom.xml、src 目录等所有项目文件
COPY . .

# 执行 Maven 打包命令：
# - package: 打包项目
# - -DskipTests: 跳过测试（加快构建速度）
# - -B: 批处理模式（非交互式，适合自动化构建）
# - -pl provider: 只构建 provider 模块（pl = projects list）
# - -am: 同时构建 provider 依赖的其他模块（am = also-make）
RUN mvn package -DskipTests -B -pl provider -am

# ============================================
# 第二阶段：运行阶段（Runtime Stage）
# 使用多阶段构建的好处：最终镜像只包含运行所需的内容，体积更小
# 这里使用 JRE（Java Runtime Environment）而不是 JDK，因为只需要运行不需要编译
# ============================================
FROM eclipse-temurin:17-jre

# 设置工作目录
WORKDIR /app

# 从第一阶段的 build 中复制打包好的 jar 文件
# --from=build 表示从名为 build 的阶段复制
# /app/provider/target/provider*.jar 是 Maven 打包生成的 jar 文件路径
# 复制到当前目录并重命名为 app.jar
COPY --from=build /app/provider/target/provider*.jar app.jar

# 暴露 20880 端口，这是 Dubbo 服务的默认端口
# 注意：这只是声明，实际映射需要在运行容器时指定 -p 参数
EXPOSE 20880

# 容器启动时执行的命令
# 使用 exec 格式（JSON 数组），这样 Java 进程会成为 PID 1，能正确接收系统信号
ENTRYPOINT ["java", "-jar", "app.jar"]
