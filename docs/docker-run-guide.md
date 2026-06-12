# Dubbo 极简案例 — Docker 运行操作手册

> **目标：** 在你自己的电脑上，从头到尾运行一遍 Dubbo 案例，看到 `[Dubbo] Hello, World!` 的输出结果。
>
> **前提假设：** 你已经安装了 Docker，但可能不太熟悉 Docker 命令。每一步都会解释"这是什么"、"为什么要这样做"。

---

## 第 0 步：确认环境（1 分钟）

### 0.1 确认 Docker 已安装

打开终端（命令行窗口），输入：

```bash
docker --version
```

**预期输出类似：**
```
Docker version 24.x.x
```

> **如果报错 `command not found` 或类似提示**，说明 Docker 还没装好。
> - **Mac:** 下载 [Docker Desktop](https://www.docker.com/products/docker-desktop/)，安装后启动即可
> - **Windows:** 同上下载 Docker Desktop，安装后重启电脑
> - **Linux (Ubuntu/Debian):** 运行 `sudo apt install docker.io docker-compose-v2`
>
> 装好后**重新打开终端**再试一次。

### 0.2 确认 Docker Compose 可用

```bash
docker compose version
```

**预期输出类似：**
```
Docker Compose version v2.x.x
```

> 注意是 `docker compose`（v2 写法），不是旧版的 `docker-compose`（带横杠）。
> 如果只有 `docker-compose`，那下面的命令把 `docker compose` 换成 `docker-compose` 即可。

### 0.3 进入项目目录

```bash
cd /root/projects/20260612-learn-dubbo
```

然后确认文件都在：

```bash
ls -la
```

**你应该能看到这些文件/文件夹：**
```
docker-compose.yml    ← 编排文件（指挥官）
pom.xml               ← Maven 父配置
api/                  ← 接口定义模块
provider/             ← 服务提供者模块
consumer/             ← 服务消费者模块
.dockerignore         ← Docker 构建时忽略的文件
```

---

## 第 1 步：理解你要做的事情（先看懂，再动手）

### 用生活类比理解整个流程

想象你要开一家连锁餐饮店，需要三样东西：

```
┌─────────────────────────────────────────────────────┐
│                                                     │
│   ┌──────────┐     ┌──────────┐     ┌──────────┐   │
│   │Zookeeper │     │ Provider │     │ Consumer │   │
│   │  外卖平台 │     │  后厨    │     │  顾客APP │   │
│   └──────────┘     └──────────┘     └──────────┘   │
│                                                     │
│   第一步：搭建外卖平台                               │
│   第二步：后厨开业，在平台上注册                      │
│   第三步：顾客下单，收到餐品                          │
│                                                     │
└─────────────────────────────────────────────────────┘
```

Docker 要做的事就是：
1. **构建** — 把 Java 代码打包成可运行的「容器镜像」（像把菜谱做成预制菜）
2. **编排启动** — 按顺序启动三个容器（像按顺序开店）
3. **观察日志** — 看输出结果（像查看订单状态）

---

## 第 2 步：一键构建并启动（核心命令）

### 2.1 执行命令

在项目根目录下（确保你能看到 `docker-compose.yml` 这个文件），执行：

```bash
docker compose up --build
```

**逐词拆解这个命令：**

| 单词 | 含义 | 为什么需要 |
|------|------|-----------|
| `docker` | Docker 客户端程序 | 你和 Docker 说话的工具 |
| `compose` | 编排模式 | 同时管理多个容器 |
| `up` | 启动 | 创建并启动容器 |
| `--build` | 先构建镜像 | 强制重新编译 Java 代码（而不是用旧的缓存） |

> **为什么加 `--build`？**
> 因为你可能修改了代码。不加的话，Docker 可能会用之前缓存的旧镜像，导致你的修改不生效。

### 2.2 你会看到什么？（完整过程预览）

执行命令后，终端会开始滚动输出。整个过程大约 **3-5 分钟**（首次需要下载基础镜像）。

输出分为 **4 个阶段**：

#### 阶段一：下载基础镜像（仅首次）

```
 => Pulling from library/eclipse-temurin    ← 下载 Java 运行环境
 => Pulling from library/maven              ← 下载 Maven 编译工具
 => Pulling from library/zookeeper           ← 下载 Zookeeper 镜像
```

> 这就像第一次做饭要去超市买食材。买过一次后，食材就存在你家冰箱里了（Docker 会缓存镜像），下次就不用再买了。

#### 阶段二：Maven 编译 Java 代码

```
 => [provider build] RUN mvn package ...    ← 编译 Provider 模块
 => [consumer build] RUN mvn package ...    ← 编译 Consumer 模块
```

> 这一步最慢（约 2-3 分钟）。Maven 在容器内下载所有依赖 jar 包，然后编译打包。
> 你会看到大量 `Downloading from central: ...` 的输出，这是正常的。

#### 阶段三：三个容器依次启动

```
 ✔ Container zookeeper-1  Created          ← Zookeeper 先启动
 ✔ Container provider-1    Created          ← Provider 再启动
 ✔ Container consumer-1    Created          ← Consumer 最后启动
Attaching to consumer-1, provider-1, zookeeper-1
```

> 启动顺序很重要！Consumer 必须等 Provider 启动完成才能找到服务。

#### 阶段四：关键输出！（盯着这里）

Provider 日志中会出现：
```
>>> Dubbo Provider started!
```

Consumer 日志中会出现（这就是成功标志！）：
```
>>> Consumer received: [Dubbo] Hello, World!
consumer-1 exited with code 0                ← 正常退出
```

> 看到 `[Dubbo] Hello, World!` 就说明 **Dubbo RPC 调用成功了！**

---

## 第 3 步：验证结果（确认你真的跑通了）

### 3.1 方法一：回头看终端输出

在上一步的终端输出中，搜索（Ctrl+F）以下关键词：

- `>>> Dubbo Provider started!` — 说明 Provider 启动成功
- `>>> Consumer received:` — 说明 Consumer 收到了远程调用的返回值
- `[Dubbo] Hello, World!` — 这就是 RPC 调用的实际返回值！

### 3.2 方法二：用 docker compose logs 单独查看

如果终端输出滚过去了，可以用这条命令单独看 Consumer 的日志：

```bash
docker compose logs consumer | grep "Consumer received"
```

**预期输出：**
```
consumer-1  | >>> Consumer received: [Dubbo] Hello, World!
```

> `docker compose logs consumer` = 只看 consumer 这个容器的日志
> `grep "Consumer received"` = 只显示包含这行字的日志（过滤掉其他杂乱信息）

### 3.3 方法三：看 Provider 的服务注册日志

```bash
docker compose logs provider | grep -E "Export dubbo service|Register dubbo"
```

**预期输出：**
```
provider-1  | Export dubbo service com.example.api.GreetingService ...
provider-1  | Register dubbo service ... to registry zookeeper:2181
```

> 这两行说明：
> - `Export` = Provider 把 GreetingService 服务暴露出来了
> - `Register` = Provider 把自己注册到了 Zookeeper 注册中心

---

## 第 4 步：清理（做完之后收拾现场）

跑完之后，容器还占着内存和端口。建议清理掉：

```bash
docker compose down
```

**预期输出：**
```
 ✔ Container consumer-1    Removed
 ✔ Container provider-1    Removed
 ✔ Container zookeeper-1   Removed
 ✔ Network xxx_default      Removed
```

> `down` = 停止并删除所有容器和网络。
> 但**不会删除镜像**（下次 `up --build` 时如果代码没变，会快很多）。

### 如果想彻底清理（连镜像也删）：

```bash
docker compose down --rmi all
```

> **慎用！** 删除镜像后下次要重新下载+编译，耗时较长。
> 一般情况只用 `docker compose down` 就够了。

---

## 第 5 步：反复练习（推荐多跑几遍）

### 5.1 重新运行

```bash
# 清理上次的
docker compose down

# 重新构建并启动
docker compose up --build
```

### 5.2 修改参数试试

比如修改 Consumer 的调用参数，让它传不同的名字：

编辑文件 [consumer/ConsumerApplication.java](consumer/src/main/java/com/example/consumer/ConsumerApplication.java)，找到这一行：

```java
String result = greetingService.sayHello("World");
```

改成：

```java
String result = greetingService.sayHello("Dubbo");
```

然后重新运行：

```bash
docker compose up --build
```

这次你应该看到：

```
>>> Consumer received: [Dubbo] Hello, Dubbo!
```

> **恭喜！你已经完成了 Dubbo 的第一次自定义修改和验证！**

---

## 常见问题排查（遇到问题看这里）

### 问题 1：`port already in use`（端口被占用）

**现象：**
```
Error: bind: address already in use
```

**原因：** 上次运行的容器没关掉，端口还被占用着。

**解决：**
```bash
docker compose down        # 先清理
docker compose up --build  # 再启动
```

### 问题 2：`Cannot connect to the Docker daemon`

**现象：**
```
Cannot connect to the Docker daemon. Is the docker daemon running?
```

**原因：** Docker 服务没启动。

**解决：**
- Mac/Windows: 打开 Docker Desktop 应用，等待它完全启动（菜单栏出现鲸鱼图标）
- Linux: `sudo systemctl start docker`

### 问题 3：Maven 下载依赖超时

**现象：**
```
Could not resolve dependency / Connection timed out
```

**原因：** 网络问题，Maven 中央仓库访问慢。

**解决：**
- 多试几次（网络波动）
- 或者配置国内镜像源（在 `pom.xml` 中添加阿里云镜像，但本案例一般不需要）

### 问题 4：Consumer 报错连接不上 Zookeeper

**现象：**
```
Connection refused: zookeeper:2181
```

**原因：** Zookeeper 还没完全启动，Consumer 就尝试连接了。

**解决：** 这种情况一般是暂时的。如果持续报错，手动调整启动顺序：
```bash
# 先只启动 ZK 和 Provider
docker compose up zookeeper provider --build

# 等 Provider 输出 ">>> Dubbo Provider started!" 后，
# 另开一个终端，只启动 Consumer
docker compose up consumer --build
```

### 问题 5：想看实时日志但不想滚动太多

使用 `-f` 参数（follow = 跟踪实时输出）：

```bash
docker compose logs -f consumer
```

> 终端会持续跟踪 Consumer 的日志输出。按 `Ctrl+C` 退出。

---

## 附录：常用命令速查表

| 命令 | 作用 |
|------|------|
| `docker compose up --build` | 构建并启动所有服务 |
| `docker compose up -d --build` | 构建并在后台启动（不占终端） |
| `docker compose logs` | 查看所有容器日志 |
| `docker compose logs provider` | 只看 Provider 日志 |
| `docker compose logs -f consumer` | 实时跟踪 Consumer 日志 |
| `docker compose ps` | 查看所有容器状态 |
| `docker compose down` | 停止并删除所有容器 |
| `docker compose restart` | 重启所有容器 |
| `docker images` | 查看本地所有镜像 |

---

## 最终检验清单

跑通之后，确认你看到了以下 **4 个关键信号**：

- [ ] 1. Zookeeper 启动成功（没有报错退出）
- [ ] 2. Provider 输出 `>>> Dubbo Provider started!`
- [ ] 3. Provider 输出 `Register dubbo service ... to registry zookeeper:2181`
- [ ] 4. Consumer 输出 `>>> Consumer received: [Dubbo] Hello, World!` 并以 `exited with code 0` 退出

全部打钩 = **恭喜，你已经成功跑通了第一个 Dubbo RPC 案例！**
