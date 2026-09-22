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

如果端口冲突，可以先执行：

```powershell
netstat -ano | findstr 8719
netstat -ano | findstr 8858
```

## 6. 备注

本项目使用的是 Spring Boot 3.x + Spring Cloud 2023 + Spring Cloud Alibaba 2023，运行环境要求：

- JDK 17
- Maven
- Nacos 2.3.x

如果你在开发过程中遇到启动问题，可以先检查 Nacos 控制台是否正常运行，再定位具体服务配置问题。

另外，Sentinel Dashboard 和应用的连接依赖于客户端 transport port。如果你看到 Dashboard 没有接收到机器信息，优先检查 `8719` 是否被占用，并确认 order-service 已成功启动。

推荐启动顺序：

1. 启动 Nacos
2. 启动 sentinel-dashboard
3. 启动 gateway
4. 启动 user-service
5. 启动 order-service
6. 在 Dashboard 中查看是否出现机器列表或簇点链路
