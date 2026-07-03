# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此仓库中工作时提供指导。

## 构建命令

```bash
# 完整构建（按惯例跳过测试，与 CI 工作流一致）
mvn clean package -Dmaven.test.skip=true

# 完整构建（含测试）
mvn clean package

# 构建单个模块及其依赖
mvn clean install -pl yudao-module-system/yudao-module-system-server -am

# 运行单个模块的测试
mvn test -pl yudao-module-system/yudao-module-system-server

# 运行单个测试类
mvn test -pl yudao-module-system/yudao-module-system-server -Dtest=AdminUserServiceImplTest

# 运行单个测试方法
mvn test -pl yudao-module-system/yudao-module-system-server -Dtest=AdminUserServiceImplTest#testCreateUser
```

根 POM 使用 `${revision}`（版本号：2026.06-jdk8-SNAPSHOT）配合 `flatten-maven-plugin` 插件。在针对单个模块操作时，务必使用 `-pl` / `-am` 参数。

## 架构

### 双模块模式（API / Server）

每个业务模块严格遵循二子模块拆分：

- **`yudao-module-{name}-api`** — 包含 Feign 接口、DTO、枚举以及消息队列事件定义。这是其他模块依赖的契约层。
- **`yudao-module-{name}-server`** — 包含 Controller、Service 实现、Mapper、领域对象。依赖自身的 `-api` 模块以及需要调用的其他 `-api` 模块。

模块之间只能依赖彼此的 `-api` jar 包，绝不能依赖 `-server`。服务间调用通过 `-api` 模块中定义的 Feign 接口进行。

### 部署模式

- **单体模式**：所有 `-server` 模块作为 `yudao-server` 的依赖引入，`yudao-server` 将所有内容打包为一个独立的 fat JAR。使用 `yudao-server/src/main/resources/application.yaml` 并将 active profile 设置为 `local`。
- **微服务模式**：每个 `-server` 模块独立运行。启动 `yudao-gateway`（端口 48080）以及所需的各个服务。

### 核心框架模块（`yudao-framework/`）

| Starter | 用途 |
| --- | --- |
| `yudao-common` | 基础 POJO、枚举（`CommonStatusEnum`）、`Result`/`PageResult`、工具类 |
| `yudao-spring-boot-starter-web` | REST API 封装、全局异常处理、Swagger/Knife4j 配置 |
| `yudao-spring-boot-starter-security` | Spring Security + Token 认证、`@PreAuthorize` 支持、SSO 单点登录 |
| `yudao-spring-boot-starter-mybatis` | MyBatis Plus + 连表插件、多租户/数据权限拦截器、`BaseMapperX`/`BaseMapperUtil` |
| `yudao-spring-boot-starter-redis` | Redisson + Redis 缓存、分布式锁支持 |
| `yudao-spring-boot-starter-mq` | 可插拔的消息队列抽象层，支持 RocketMQ/RabbitMQ/Kafka/Redis |
| `yudao-spring-boot-starter-job` | XXL-Job 集成 |
| `yudao-spring-boot-starter-monitor` | Spring Boot Admin + SkyWalking traceId 日志 |
| `yudao-spring-boot-starter-protection` | Sentinel 限流、Lock4j 分布式锁、API 加密 |
| `yudao-spring-boot-starter-rpc` | OpenFeign 集成 |
| `yudao-spring-boot-starter-excel` | 基于 FastExcel 的 Excel 导入导出 |
| `yudao-spring-boot-starter-websocket` | WebSocket，支持 Redis/RocketMQ/Kafka/RabbitMQ 集群模式 |
| `yudao-spring-boot-starter-biz-tenant` | SaaS 多租户（按 `tenant_id` 隔离数据） |
| `yudao-spring-boot-starter-biz-data-permission` | 行级数据权限过滤 |
| `yudao-spring-boot-starter-test` | 测试基类（见测试章节） |

### 包结构（每个 `-server` 模块内部）

```
cn.iocoder.yudao.module.{name}
├── controller/admin/   # 管理后台 REST 控制器
├── controller/app/     # 用户端 REST 控制器（会员端）
├── convert/            # MapStruct 转换器（XxxConvert.INSTANCE）
├── dal/dataobject/     # MyBatis Plus 实体（mapper XML 在 resources/mapper/ 下）
├── dal/mysql/          # Mapper 接口
├── enums/              # 模块特有枚举 + `@DictFormat` 注解
├── framework/          # 模块级配置、拦截器、安全相关
├── job/                # XXL-Job 处理器
├── mq/                 # 消息队列生产者与消费者
├── service/            # Service 接口与实现
└── websocket/          # WebSocket 消息处理器
```

## 配置

- **本地环境**：各模块 `src/main/resources/` 下的 `application-local.yaml` 文件。使用 `spring.profiles.active=local`。
- **Nacos**：生产环境配置外部化到 Nacos。各服务通过 `optional:nacos:${spring.application.name}-${spring.profiles.active}.yaml` 导入。
- **数据库初始化脚本**：`sql/` 目录，按数据库类型组织（MySQL、Oracle、PostgreSQL、SQL Server、DM、Kingbase、OpenGauss、Highgo、DB2）。
- **本地数据库 Docker Compose**：`sql/tools/docker-compose.yaml`，可启动 MySQL、PostgreSQL、Oracle XE、SQL Server、DM8、Kingbase、OpenGauss、Highgo。

## 测试

测试基类位于 `yudao-framework/yudao-spring-boot-starter-test`：

| 基类 | 适用场景 |
| --- | --- |
| `BaseMockitoUnitTest` | 纯 Mockito，不启动 Spring 上下文，速度最快。无需 DB/Redis 时使用。 |
| `BaseDbUnitTest` | H2 内存数据库 + MyBatis Plus。每个测试后通过 `clean.sql` 自动清理表数据。Profile：`unit-test`。 |
| `BaseDbAndRedisUnitTest` | H2 内存数据库 + 嵌入式 Redis（Jedis-Mock）。需要同时使用 DB 和 Redis 时使用。 |
| `BaseRedisUnitTest` | 仅嵌入式 Redis。 |

使用 `@MockBean` 模拟外部服务调用。测试 DTO 使用 Builder 模式构建（所有 DTO 和数据对象均使用 Lombok `@Builder`）。

## 服务启动（本地开发）

前置条件：MySQL、Redis、Nacos。可使用 `sql/tools/docker-compose.yaml` 启动数据库。

```bash
# 1. 启动 Nacos（单机模式）
# 2. 启动单个服务，例如：
mvn spring-boot:run -pl yudao-module-system/yudao-module-system-server -Dspring.profiles.active=local
# 或者启动单体应用：
mvn spring-boot:run -pl yudao-server -Dspring.profiles.active=local
```

网关运行在 **48080** 端口。各服务端口定义在各自模块的 `application.yaml` 中。

## 前端

前端项目以 git 子模块形式位于 `yudao-ui/` 目录下：
- `yudao-ui-admin-vue3`（Vue 3 + Element Plus）— 主力管理后台 UI
- `yudao-ui-admin-vben`（Vue 3 + Ant Design Vue）— 备选管理后台 UI
- `yudao-ui-admin-vue2`（Vue 2 + Element UI）
- `yudao-ui-admin-uniapp`（Uni-App）— 移动端管理后台
- `yudao-ui-mall-uniapp`（Uni-App）— 移动端商城

## 分支策略

| 分支 | JDK | Spring Boot | Spring Cloud |
|---|---|---|---|
| `master`（当前） | 8 | 2.7.18 | 2021.0.9 |
| `master-jdk17` | 17/21 | 3.5.x | 2024.x |
| `master-jdk25` | 25 | 4.x | 2025.x |

`yudao-module-ai` 模块需要 JDK 17+（基于 Spring AI），在 `master` 分支的根 POM 中默认被注释掉。