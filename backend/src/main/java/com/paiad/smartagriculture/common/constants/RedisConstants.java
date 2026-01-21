package com.paiad.smartagriculture.common.constants;

/**
 * Redis 相关常量
 */
public final class RedisConstants {

    private RedisConstants() {
    }

    /**
     * Key 前缀
     */
    public static final String KEY_PREFIX = "smart-agri:";

    /**
     * 告警规则缓存 Key
     */
    public static final String ALARM_RULES_KEY = KEY_PREFIX + "alarm-rules";

    /**
     * 告警规则缓存 TTL（小时）
     */
    public static final long ALARM_RULES_TTL_HOURS = 1;

    /**
     * 告警抑制 Key 前缀: smart-agri:alarm:suppression:{deviceId}:{metric}
     */
    public static final String ALARM_SUPPRESSION_PREFIX = KEY_PREFIX + "alarm:suppression:";

    /**
     * 告警抑制窗口 TTL（分钟）
     * 同设备同指标在此时间内不重复生成告警
     */
    public static final long ALARM_SUPPRESSION_TTL_MINUTES = 5;
}
