# learn-deu-demo

这是一个基于 Spring Cloud Alibaba 的学习示例项目，当前使用 JDK 17 运行环境。

## 1. 项目说明

本项目包含以下模块：

- common：公共模块，放置公共实体、统一返回结果等
- user-service：用户服务
- order-service：订单服务
- gateway：网关模块

## 2. Kafka 生产者 / 消费者示例

本仓库新增了两个独立的 Kafka demo 模块：

- `kafka-producer-demo`：生产者服务，负责向 `demo-topic` 发送消息
- `kafka-consumer-demo`：消费者服务，监听 `demo-topic` 并消费消息

### 2.1 启动 Kafka

在仓库根目录执行：

```bash
docker compose up -d
```

这会启动一个本地单机 Kafka + Zookeeper 环境，默认地址为：

- Kafka：`localhost:9092`
- Zookeeper：`localhost:2181`

### 2.2 启动生产者和消费者

分别在两个终端运行：

```bash
mvn spring-boot:run -pl kafka-producer-demo
mvn spring-boot:run -pl kafka-consumer-demo
```

### 2.3 发送消息测试

发送一条消息到 Kafka：

```bash
curl -X POST http://localhost:8081/api/messages/send \
  -H "Content-Type: application/json" \
  -d '{"message":"hello kafka"}'
```

如果正常，消费者控制台中会输出类似日志：

```text
Received Kafka message: hello kafka
```

### 2.4 说明

- 生产者模块默认发送到 `demo-topic`
- 消费者模块默认监听同一个 `demo-topic`
- 如果需要改成你自己的主题名，可修改 `demo-topic` 常量和配置

## 3. Nacos 配置中心 / 注册中心

已下载并启动 Nacos，目录如下：

D:\work\software\nacos-server-2.3.2\nacos

### 启动方式（Windows）

进入 Nacos 安装目录的 bin 目录，执行：

```bat
cd /d D:\work\software\nacos-server-2.3.2\nacos\bin
startup.cmd -m standalone
```

说明：
- `-m standalone` 表示单机模式启动
- 启动成功后，可以访问：
  - 控制台地址： http://localhost:8848/nacos
  - 默认账号： nacos
  - 默认密码： nacos

### 停止方式

```bat
cd /d D:\work\software\nacos-server-2.3.2\nacos\bin
shutdown.cmd
```

## 3. Sentinel Dashboard 启动方式

在本地启动 Sentinel Dashboard 的推荐命令如下：

```bash
java -Dserver.port=8858 -Dcsp.sentinel.dashboard.server=localhost:8858 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.9.jar
```

启动成功后，可访问：

- Dashboard 地址：`http://localhost:8858`
- 默认账号：`sentinel`
- 默认密码：`sentinel`

如果你使用的是 Windows PowerShell，也可以直接这样执行：

```powershell
java -Dserver.port=8858 -Dcsp.sentinel.dashboard.server=localhost:8858 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.9.jar
```

## 4. 本项目启动顺序

建议按下面顺序启动：

1. 启动 Nacos
2. 启动 sentinel-dashboard
3. 启动 gateway
4. 启动 user-service
5. 启动 order-service


## 5. 常见问题

### 5.1 启动 Nacos 报错

检查以下几点：
- Java 版本是否为 17+
- 是否有端口冲突（默认端口 8848）
- 是否有权限访问安装目录

### 5.2 服务注册不成功

确认：
- Nacos 已正常启动
- `application.yml` / `bootstrap.yml` 中的 Nacos 地址配置正确
- 服务名和命名空间配置无误

### 5.3 Sentinel Dashboard 没有应用数据

确认：
- `sentinel-dashboard` 已启动并能访问 `http://localhost:8858`
- `spring.cloud.sentinel.transport.dashboard=127.0.0.1:8858` 已配置
- `order-service` 已正常启动，并访问过 `/order/sentinel/demo`

## 6. 备注

本项目使用的是 Spring Boot 3.x + Spring Cloud 2023 + Spring Cloud Alibaba 2023，运行环境要求：

- JDK 17
- Maven
- Nacos 2.3.x

如果你在开发过程中遇到启动问题，可以先检查 Nacos 控制台是否正常运行，再定位具体服务配置问题。


推荐启动顺序：

1. 启动 Nacos
2. 启动 sentinel-dashboard
3. 启动 gateway
4. 启动 user-service
5. 启动 order-service
6. 在 Dashboard 中查看是否出现机器列表或簇点链路
