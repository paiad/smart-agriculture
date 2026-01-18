# Smart Agriculture (智慧农业后端)

基于 JDK 21 + Spring Boot 3.3.6 + MyBatis-Plus 重构的智慧农业后端系统。

## 技术栈

- **Language**: Java 21
- **Framework**: Spring Boot 3.3.6
- **Database**: MySQL 8.0, Redis
- **ORM**: MyBatis-Plus
- **Auth**: JWT (Phone + Password)
- **Docs**: Knife4j (Swagger)

## 快速开始

1. 配置 `application-dev.yml` 中的 MySQL 和 Redis 连接。
2. 运行 `Application`。
3. 访问接口文档：`http://localhost:8080/doc.html`

## 项目结构

- `com.paiad.smartagriculture`
  - `common`: 公共组件 (Result, Utils)
  - `config`: 全局配置
  - `controller`: Web 接口
  - `service`: 业务逻辑
  - `mapper`: 数据访问
  - `pojo`: 实体对象 (Entity, DTO, VO)
