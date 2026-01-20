# 项目开发任务记录 (Task.md)

## 2026-01-20 MQTT 与 告警系统深度优化

### 1. MQTT 异步化与高效处理

- [x] **异步线程池**：创建 `mqttTaskExecutor` 线程池（2-5 核心线程），实现 `EnvDataService.processAndSave` 的 `@Async` 执行，解决 MQTT 回调阻塞问题。
- [x] **Topic 规范化**：将 Topic 前缀由 `smart-agriculture` 优化为 `smart-agri`（节省字节），定义 `data/{deviceId}` 和 `cmd/{deviceId}` 结构。
- [x] **多重校验机制**：
  - 校验上报 `deviceId` 是否在数据库中注册。
  - 校验 Topic 中的 `deviceId` 与 Payload 中的是否一致。
  - **性能优化**：调整校验顺序，先执行内存级的 JSON 解析与一致性比对，最后执行数据库查询。

### 2. 告警检测与 Redis 缓存

- [x] **规则缓存化**：实现 `smart-agri:alarm-rules` 的 Redis Hash 存储，告警检测由“查库”改为“查缓存”，极大降低 MySQL 高频 IO。
- [x] **缓存同步策略 (Cache-Aside)**：重写 `AlarmRuleService` 的保存、更新、删除方法，确保管理端修改规则后 Redis 实时同步。
- [x] **自动回源与 TTL**：
  - 增加缓存失效时的自动数据库回源并重载。
  - 增加 1 小时 TTL 兜底，确保数据的最终一致性。

### 3. 常量化重构与架构治理

- [x] **统一常量管理**：在 `common.constants` 下创建 `MqttConstants`、`RedisConstants`、`MetricConstants`。
- [x] **代码去硬编码**：全局清理 `EnvDataServiceImpl`、`ControlCommandController` 等类中的硬编码字符串（Topic、Redis Key、指标名如 `sHR`, `TMP` 等）。
- [x] **安全配置**：在 `application-dev.yml` 中添加 Redis 密码配置并统一配置项。

---

_记录人：Antigravity (AI Coding Assistant)_

### 4. 控制指令闭环 (已完成)

- [x] **ACK 机制**：新增 `smart-agri/ack/{deviceId}` 主题订阅。
- [x] **状态自动更新**：后端 `MqttService` 解析 ACK 消息，根据 `requestId` 更新指令状态（成功/失败）及确认时间。
- [x] **ID 追踪**：控制接口返回生成的 `requestId`，方便前端轮询状态。
- [x] **JSON 优化**：修复了 `params` 字段 JSON 转义问题，确保下发给设备的 Payload 格式标准。

### 5. 设备心跳与在线状态 (已完成)

- [x] **自动保活**：在收到设备数据上报或 ACK 确认时，自动更新设备的 `online=1` 和 `lastSeenAt`。
- [x] **离线检测**：实现 `@Scheduled` 定时任务，每分钟扫描一次，将超过 5 分钟无活跃的设备标记为 `online=0`。

### 6. 文档增强 (已完成)

- [x] **Knife4j 补全**：修复 `OpenApiConfig` 分组配置，并为核心 Controller 和 POJO 添加 Swagger 注解。
