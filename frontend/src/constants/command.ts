/**
 * 指令状态常量 (与后端 CommandConstants 对应)
 */
export const CommandStatus = {
  PENDING: 0,   // 待发送
  SENT: 1,      // 已发送
  SUCCESS: 2,   // 成功
  FAILED: 3,    // 失败
  TIMEOUT: 4    // 超时
} as const

export type CommandStatusType = typeof CommandStatus[keyof typeof CommandStatus]

/**
 * 指令状态文本映射
 */
export const CommandStatusText: Record<number, string> = {
  [CommandStatus.PENDING]: '待发送',
  [CommandStatus.SENT]: '已发送',
  [CommandStatus.SUCCESS]: '成功',
  [CommandStatus.FAILED]: '失败',
  [CommandStatus.TIMEOUT]: '超时'
}

/**
 * 指令状态对应的标签类型 (Element Plus Tag)
 */
export const CommandStatusTagType: Record<number, 'info' | 'warning' | 'success' | 'danger'> = {
  [CommandStatus.PENDING]: 'info',
  [CommandStatus.SENT]: 'warning',
  [CommandStatus.SUCCESS]: 'success',
  [CommandStatus.FAILED]: 'danger',
  [CommandStatus.TIMEOUT]: 'danger'
}

/**
 * 指令超时时间（秒）
 */
export const COMMAND_TIMEOUT_SECONDS = 30
