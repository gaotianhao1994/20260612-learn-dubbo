# 极简 Dubbo 学习案例设计

## 目标
使用 Docker 编排运行一个极简的 Dubbo 3.x 案例，形成 Dubbo 初步直觉。

## 架构
- **Zookeeper**: 注册中心 (端口 2181)
- **Dubbo Provider**: 服务提供者，暴露 GreetingService (端口 20880)
- **Dubbo Consumer**: 服务消费者，远程调用 GreetingService

## 技术栈
- Java 17, Dubbo 3.3.x, Spring Boot 3.2.x, Zookeeper 3.8
- Maven 多模块 + Docker Compose

## 项目结构
```
20260612-learn-dubbo/
├── docker-compose.yml
├── pom.xml (父 POM)
├── api/          # 共享接口
├── provider/     # 服务提供者
└── consumer/     # 服务消费者
```

## 核心服务
`GreetingService.sayHello(String name)` → 返回 `[Dubbo] Hello, {name}!`

## 运行流程
1. Docker Compose 启动 Zookeeper
2. Provider 启动 → 注册服务到 Zookeeper
3. Consumer 启动 → 发现服务 → RPC 调用 → 打印结果
