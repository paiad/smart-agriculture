# 项目问题与优化建议 (Problem.md)

> 审查日期：2026-01-21

---

## 一、待解决问题

### 1. ~~MQTT 连接失败导致应用无法启动~~ ✅ 已解决

**优先级**：中高

**位置**：`MqttConfig.java`

**已修复方案**：

- [x] 使用 `@Lazy` 延迟初始化 MqttClient
- [x] 连接失败时不抛出异常，应用正常启动
- [x] 添加 30 秒定时重连机制 (`@Scheduled`)
- [x] `MqttService` 增加 null 检查和连接状态判断
- [ ] 添加健康检查端点 `/actuator/health/mqtt` (可选)

---

### 2. ~~MQTT 回调异步化确认~~ ✅ 已确认

**优先级**：中

**位置**：`MqttService.messageArrived()` → `EnvDataService.processAndSave()`

**确认结果**：异步配置已完善 ✅

- [x] `EnvDataService.processAndSave()` 已标注 `@Async("mqttTaskExecutor")`
- [x] `AsyncConfig` 已配置 `mqttTaskExecutor` 线程池 (2-5 核心线程, 队列 100)
- [x] 使用 `CallerRunsPolicy` 拒绝策略，队列满时回退到调用线程

---

### 3. ~~超时常量重复定义~~ ✅ 已解决

**优先级**：低

**位置**：

- `CommandTimeoutTask.java`
- `ControlCommandController.java`

**已修复方案**：

- [x] 创建 `common/constants/CommandConstants.java`
- [x] 统一定义 `COMMAND_TIMEOUT_SECONDS = 30`
- [x] 定义状态码常量 `STATUS_PENDING/SENT/SUCCESS/FAILED/TIMEOUT`
- [x] 更新引用类使用常量

---

### 4. ~~告警去重/抑制机制缺失~~ ✅ 已解决

**优先级**：中

**位置**：`EnvDataServiceImpl.createAlarm()`

**已修复方案**（采用方案 A）：

- [x] Redis Key: `smart-agri:alarm:suppression:{deviceId}:{metric}`
- [x] 5 分钟 TTL 抑制窗口，同设备同指标不重复告警
- [x] 告警生成后自动设置抑制标记
- [x] 抑制期间跳过告警，仅记录 debug 日志

---

## 二、未来优化建议

### 5. env_data 表分区/归档策略

**优先级**：未来

**问题**：IoT 场景数据量大，`env_data` 作为时序数据会快速膨胀

**建议**：

- [ ] MySQL 按时间分区（按月/按周）
- [ ] 定期归档历史数据到冷存储
- [ ] 或迁移到时序数据库（TimescaleDB / InfluxDB）

---

### 6. 指令状态 WebSocket 推送

**优先级**：可选

**现状**：前端通过轮询 `GET /status/{requestId}` 查询指令状态

**问题**：轮询效率低，实时性差

**建议**：

- [ ] 使用 WebSocket 推送指令状态变化
- [ ] 或使用 SSE (Server-Sent Events)

---

## 三、已完成的优化 ✅

| 优化项                   | 状态 | 说明                                           |
| ------------------------ | ---- | ---------------------------------------------- |
| 校验顺序优化 (内存 → IO) | ✅   | JSON 解析 → ID 一致性 → 数据库查询             |
| Redis 缓存告警规则       | ✅   | 避免高频查库                                   |
| 设备 ID 双重校验         | ✅   | Topic vs Payload 防止伪造                      |
| ACK 闭环机制             | ✅   | 指令状态完整追踪                               |
| 指令超时定时任务         | ✅   | 30 秒超时自动标记                              |
| 设备离线检测             | ✅   | 5 分钟无活跃自动下线                           |
| 常量统一管理             | ✅   | MqttConstants, RedisConstants, MetricConstants |

---

## 四、问题追踪

| #   | 问题              | 优先级 | 状态      | 负责人 | 备注             |
| --- | ----------------- | ------ | --------- | ------ | ---------------- |
| 1   | MQTT 连接失败崩溃 | 中高   | ✅ 已解决 |        | @Lazy + 定时重连 |
| 2   | MQTT 回调异步化   | 中     | ✅ 已确认 |        | @Async 已配置    |
| 3   | 超时常量重复      | 低     | ✅ 已解决 |        | CommandConstants |
| 4   | 告警去重机制      | 中     | ✅ 已解决 |        | Redis 5分钟抑制  |
| 5   | 数据表分区        | 未来   | ⚪ 规划中 |        |                  |
| 6   | WebSocket 推送    | 可选   | ⚪ 规划中 |        |                  |
