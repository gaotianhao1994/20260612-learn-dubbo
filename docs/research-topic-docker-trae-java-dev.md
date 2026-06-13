# 待研究课题：Docker + Trae CN IDE + Java 个人开发环境

> **创建时间：** 2026-06-13
> **状态：** 待研究
> **背景：** 在学习 Dubbo 极简案例过程中产生的需求

---

## 背景

当前使用 Trae CN IDE（类 VS Code 编辑器）进行 Java 开发和 Dubbo 学习，遇到以下痛点：

### 痛点
1. **无法像 IDEA 一样直接 Debug** — 目前只能通过 `docker compose logs` 查看运行结果
2. **Docker 与 Java 开发的结合方式不熟悉** — 不清楚如何在本地开发时高效利用 Docker
3. **Trae CN IDE 的 Java 开发能力待探索** — 包括调试、热部署、代码补全等

### 当前可接受的方式
- 通过 Docker Compose 构建并运行项目
- 通过日志（`docker compose logs`）查看输出结果
- 这种方式适合验证性运行，但不适合日常开发调试

## 研究目标

探索一套基于以下技术栈的个人开发工作流：

| 技术 | 角色 |
|------|------|
| **Docker / Docker Compose** | 环境隔离、依赖管理、快速验证 |
| **Trae CN IDE** | 代码编辑器（类似 VS Code） |
| **Java + Maven** | 开发语言和构建工具 |
| **Dubbo** | 核心学习/研究对象 |

## 待研究问题

### Docker 相关
- [ ] Docker 容器内的 Java 应用如何与本地 IDE 联调？
- [ ] 是否可以使用远程 Debug（Remote Debug）连接到 Docker 内的 Java 进程？
- [ ] Docker Compose 的开发模式 vs 生产模式有什么区别？
- [ ] 如何实现代码修改后快速生效（热加载 / 快速重建）？

### Trae CN IDE 相关
- [ ] Trae CN IDE 对 Java 项目支持程度如何？（代码补全、重构、导航）
- [ ] 是否支持 Java Debug？（断点、变量查看、调用栈）
- [ ] Trae CN IDE 的终端集成体验如何？
- [ ] 有哪些插件/扩展可以增强 Java 开发体验？

### 综合工作流
- [ ] 推荐的开发流程是什么？（写代码 → 本地测试 → Docker 验证 → 提交）
- [ ] 日志查看之外，还有哪些方式观察程序行为？
- [ ] 如何在保持 Docker 环境一致性的同时，获得接近本地开发的体验？

## 参考资料

- [Docker 官方文档 - Java 开发最佳实践](https://docs.docker.com/language/java/)
- [Java Remote Debug with Docker](https://www.baeldung.com/dockerizing-a-spring-boot-app#remote-debugging)
- [Dubbo 官方文档](https://dubbo.apache.org/zh/docs3-v2/java/api/)

## 备注

此课题服务于 **Dubbo 学习**这一主目标。研究优先级：
1. 先解决「能跑能看」的基本问题
2. 再优化开发体验（Debug、热部署等）
3. 最后形成稳定的工作流
