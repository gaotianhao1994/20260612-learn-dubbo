# 极简 Dubbo 学习案例 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 使用 Docker Compose 编排运行一个极简的 Dubbo 3.x 案例（Provider + Consumer + Zookeeper），通过 RPC 调用体验 Dubbo 的服务注册、发现与远程调用全流程。

**Architecture:** Maven 多模块项目，包含 api（接口定义）、provider（服务提供者）、consumer（服务消费者）三个子模块。使用 Zookeeper 作为注册中心，Docker Compose 编排所有容器。Provider 启动后向 Zookeeper 注册 GreetingService，Consumer 从 Zookeeper 发现服务并远程调用。

**Tech Stack:** Java 17, Dubbo 3.3.x, Spring Boot 3.2.x, Zookeeper 3.8, Maven, Docker Compose

---

### Task 1: 创建父 POM 和项目基础结构

**Files:**
- Create: `pom.xml`

- [ ] **Step 1: 创建父 POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>dubbo-demo</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    <modules>
        <module>api</module>
        <module>provider</module>
        <module>consumer</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <dubbo.version>3.3.2</dubbo.version>
        <spring-boot.version>3.2.5</spring-boot.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.apache.dubbo</groupId>
                <artifactId>dubbo-bom</artifactId>
                <version>${dubbo.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

---

### Task 2: 创建 API 模块（接口定义）

**Files:**
- Create: `api/pom.xml`
- Create: `api/src/main/java/com/example/api/GreetingService.java`

- [ ] **Step 1: 创建 API 模块的 POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>dubbo-demo</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>api</artifactId>
</project>
```

- [ ] **Step 2: 创建 GreetingService 接口**

```java
package com.example.api;

public interface GreetingService {
    String sayHello(String name);
}
```

---

### Task 3: 创建 Provider 模块（服务提供者）

**Files:**
- Create: `provider/pom.xml`
- Create: `provider/src/main/java/com/example/provider/GreetingServiceImpl.java`
- Create: `provider/src/main/java/com/example/provider/ProviderApplication.java`
- Create: `provider/src/main/resources/application.yml`
- Create: `provider/Dockerfile`

- [ ] **Step 1: 创建 Provider POM（依赖 api + dubbo + zookeeper）**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>dubbo-demo</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>provider</artifactId>

    <dependencies>
        <!-- 共享接口 -->
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>api</artifactId>
            <version>${project.version}</version>
        </dependency>
        <!-- Dubbo Spring Boot Starter -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-springboot-starter</artifactId>
        </dependency>
        <!-- Zookeeper 注册中心 -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-zookeeper</artifactId>
        </dependency>
        <!-- Spring Boot Web (可选，用于健康检查) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 创建服务实现类（@DubboService 注解暴露服务）**

```java
package com.example.provider;

import com.example.api.GreetingService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class GreetingServiceImpl implements GreetingService {
    @Override
    public String sayHello(String name) {
        return "[Dubbo] Hello, " + name + "!";
    }
}
```

- [ ] **Step 3: 创建 Provider 启动类（@EnableDubbo）**

```java
package com.example.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
        System.out.println(">>> Dubbo Provider started!");
    }
}
```

- [ ] **Step 4: 创建 Provider 配置文件 application.yml**

```yaml
spring:
  application:
    name: dubbo-provider

dubbo:
  application:
    name: dubbo-provider
  protocol:
    name: dubbo
    port: -1   # -1 表示自动分配端口（20880起）
  registry:
    address: zookeeper://zookeeper:2181
```

- [ ] **Step 5: 创建 Provider Dockerfile**

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY . .
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/provider/target/provider-*-exec.jar app.jar
EXPOSE 20880
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

### Task 4: 创建 Consumer 模块（服务消费者）

**Files:**
- Create: `consumer/pom.xml`
- Create: `consumer/src/main/java/com/example/consumer/ConsumerApplication.java`
- Create: `consumer/src/main/resources/application.yml`
- Create: `consumer/Dockerfile`

- [ ] **Step 1: 创建 Consumer POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>dubbo-demo</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>consumer</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>api</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-springboot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-zookeeper</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 创建 Consumer 启动类（@DubboReference 远程调用）**

```java
package com.example.consumer;

import com.example.api.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

@SpringBootApplication
@EnableDubbo
public class ConsumerApplication implements CommandLineRunner {

    @DubboReference
    private GreetingService greetingService;

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String result = greetingService.sayHello("World");
        System.out.println(">>> Consumer received: " + result);
        // 调用完成后退出容器，方便观察结果
        System.exit(0);
    }
}
```

- [ ] **Step 3: 创建 Consumer 配置文件 application.yml**

```yaml
spring:
  application:
    name: dubbo-consumer

dubbo:
  application:
    name: dubbo-consumer
  registry:
    address: zookeeper://zookeeper:2181
```

- [ ] **Step 4: 创建 Consumer Dockerfile**

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY . .
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/consumer/target/consumer-*-exec.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

### Task 5: 创建 Docker Compose 编排文件

**Files:**
- Create: `docker-compose.yml`

- [ ] **Step 1: 创建 docker-compose.yml**

```yaml
services:
  zookeeper:
    image: zookeeper:3.8
    ports:
      - "2181:2181"

  provider:
    build:
      context: .
      dockerfile: provider/Dockerfile
    depends_on:
      zookeeper:
        condition: service_started
    environment:
      - JAVA_OPTS=-Xms128m -Xmx256m

  consumer:
    build:
      context: .
      dockerfile: consumer/Dockerfile
    depends_on:
      provider:
        condition: service_completed_successfully
    environment:
      - JAVA_OPTS=-Xms128m -Xmx256m
```

> **关键设计说明：** consumer 设置为 `service_completed_successfully` 依赖 provider，确保 provider 先完成启动并注册服务。Consumer 执行一次调用后自动退出（System.exit(0)），日志中会打印 `[Dubbo] Hello, World!`。

---

### Task 6: 构建并运行验证

- [ ] **Step 1: 使用 Docker Compose 构建并启动所有服务**

Run: `docker compose up --build`

Expected output:
```
[+] Building ...
[+] Running 3/3
 ✔ Container ...zookeeper-1  Started
 ✔ Container ...provider-1    Started
 ✔ Container ...consumer-1    Started
```

- [ ] **Step 2: 验证 Consumer 输出**

在 consumer 容器日志中应看到：
```
>>> Consumer received: [Dubbo] Hello, World!
```

- [ ] **Step 3: 清理资源**

Run: `docker compose down`
