package com.paiad.smartagriculture.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "告警信息实体")
public class Alarm implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", hidden = true)
    private Long id;

    /**
     * 设备ID(关联 device.device_id)
     */
    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "SENSOR001")
    private String deviceId;

    /**
     * 指标编码: sHR/sEC/sPH/CO2/NN/PP/sTMP/KK/FX/ILL/HR/FS/YX/TMP/PRS/RVC
     */
    @Schema(description = "触发指标", example = "TMP")
    private String metric;

    /**
     * 触发值(数值类告警)
     */
    @Schema(description = "触发值", example = "45.5")
    private BigDecimal value;

    /**
     * 下限(触发时规则快照)
     */
    @Schema(description = "规则下限", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal minValue;

    /**
     * 上限(触发时规则快照)
     */
    @Schema(description = "规则上限", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal maxValue;

    /**
     * 告警级别:1提示 2一般 3严重
     */
    @Schema(description = "告警级别: 1提示 2一般 3严重", example = "3")
    private Integer level;

    /**
     * 状态:0未确认 1已确认 2已处理
     */
    @Schema(description = "状态: 0未确认 1已确认 2已处理", example = "0")
    private Integer status;

    /**
     * 触发时间(数据采集时间)
     */
    @Schema(description = "触发时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime triggeredAt;

    /**
     * 告警描述
     */
    @Schema(description = "告警消息", example = "温度过高")
    private String message;

    /**
     * 命中的规则ID(alarm_rule.id)
     */
    @Schema(description = "命中的规则ID", hidden = true)
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
