package com.paiad.smartagriculture.common.constants;

/**
 * 控制指令相关常量
 */
public class CommandConstants {

    private CommandConstants() {
    }

    /**
     * 指令超时时间（秒）
     */
    public static final int COMMAND_TIMEOUT_SECONDS = 30;

    /**
     * 指令状态
     */
    public static final int STATUS_PENDING = 0; // 待发送
    public static final int STATUS_SENT = 1; // 已发送
    public static final int STATUS_SUCCESS = 2; // 成功
    public static final int STATUS_FAILED = 3; // 失败
    public static final int STATUS_TIMEOUT = 4; // 超时
}
