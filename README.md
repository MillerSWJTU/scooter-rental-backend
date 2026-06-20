# Scooter Rental System - Backend

Spring Boot 3.2 单体 API 服务，为滑板车租赁系统提供 RESTful 接口。

## 技术栈

| 层 | 技术 |
|------|------|
| 框架 | Spring Boot 3.2 + Java 17 |
| 数据库 | SQLite + Spring Data JPA + Hibernate |
| 安全认证 | Spring Security + JWT (jjwt 0.11) |
| API 文档 | SpringDoc OpenAPI (Swagger UI) |
| 对象映射 | ModelMapper |
| 构建工具 | Maven |

## 架构

```
Controller → Service → Repository → Entity (DB)
    10          13          7          7
```

## 快速启动

```bash
# 前置条件: Java 17+, Maven 3.8+
mvn spring-boot:run

# API 地址: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

## 默认账户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| user | user123 | 普通用户 |

## 项目结构

```
src/main/java/com/example/scooterrent/
├── config/          # Security, JWT, CORS, Swagger, 数据初始化
├── controller/      # REST API 控制器 (10 个)
├── dto/             # 数据传输对象 (16 个)
├── entity/          # JPA 实体 → 数据库表 (7 个)
├── enums/           # 枚举字典 (6 个)
├── exception/       # 全局异常处理
├── repository/      # Spring Data JPA 数据访问 (7 个)
├── service/         # 业务逻辑 (13 个)
└── util/            # JWT 工具类
```
