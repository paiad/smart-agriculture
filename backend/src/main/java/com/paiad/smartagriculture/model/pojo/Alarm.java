package com.paiad.smartagriculture.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 告警记录实体类
 * 用于存储触发告警时的详细信息（对应设备、触发值、报警消息等）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("alarm")
public class Alarm implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备ID(关联 device.device_id)
     */
    private String deviceId;

    /**
     * 指标编码: sHR/sEC/sPH/CO2/NN/PP/sTMP/KK/FX/ILL/HR/FS/YX/TMP/PRS/RVC
     */
    private String metric;

    /**
     * 触发值(数值类告警)
     */
    private BigDecimal value;

    /**
     * 下限(触发时规则快照)
     */
    private BigDecimal minValue;

    /**
     * 上限(触发时规则快照)
     */
    private BigDecimal maxValue;

    /**
     * 告警级别:1提示 2一般 3严重
     */
    private Integer level;

    /**
     * 状态:0未确认 1已确认 2已处理
     */
    private Integer status;

    /**
     * 触发时间(数据采集时间)
     */
    private LocalDateTime triggeredAt;

    /**
     * 告警描述
     */
    private String message;

    /**
     * 命中的规则ID(alarm_rule.id)
     */
    private Long ruleId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
