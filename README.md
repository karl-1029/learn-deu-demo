# learn-deu-demo

这是一个基于 Spring Cloud Alibaba 的学习示例项目，当前使用 JDK 17 运行环境。

## 1. 项目说明

本项目包含以下模块：

- common：公共模块，放置公共实体、统一返回结果等
- user-service：用户服务
- order-service：订单服务
- gateway：网关模块

## 2. Nacos 配置中心 / 注册中心

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

## 3. 本项目启动顺序

建议按下面顺序启动：

1. 启动 Nacos
2. 启动 gateway
3. 启动 user-service
4. 启动 order-service

## 4. Sentinel 演示

当前 `order-service` 中已经加入一个最小化的 Sentinel 流控 demo，用于学习 Sentinel 的核心用法：

- 资源名：`order-sentinel-demo`
- 规则：QPS <= 2 时放行，超过 2 触发限流
- 访问地址：`GET http://localhost:8082/order/sentinel/demo`
- 复位规则：`POST http://localhost:8082/order/sentinel/reset`

关键代码位置：

- `order-service/src/main/java/com/example/order/config/SentinelFlowRuleConfig.java`
- `order-service/src/main/java/com/example/order/controller/OrderController.java`

如果你本地有 Sentinel Dashboard，可配置：

```yaml
spring:
  cloud:
    sentinel:
      eager: true
      transport:
        dashboard: 127.0.0.1:8858
        port: 8719
```

注意：只要访问频率超过 2 QPS，就会触发 `BlockException`，适合用来观察 Sentinel 对热点流量的保护效果。

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

## 6. 备注

本项目使用的是 Spring Boot 3.x + Spring Cloud 2023 + Spring Cloud Alibaba 2023，运行环境要求：

- JDK 17
- Maven
- Nacos 2.3.x

如果你在开发过程中遇到启动问题，可以先检查 Nacos 控制台是否正常运行，再定位具体服务配置问题。
