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
 * 告警规则实体类
 * 用于定义数据异常的判断标准（监测指标、阈值范围等）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("alarm_rule")
@Schema(description = "告警规则实体")
public class AlarmRule implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", hidden = true)
    private Long id;

    /**
     * 设备ID(优先级最高，非空表示只对该设备生效)
     */
    @Schema(description = "设备ID(特定设备)", example = "SENSOR001")
    private String deviceId;

    /**
     * 设备类型(次优先级，device_id为空时可用)
     */
    @Schema(description = "设备类型(通用规则)", example = "SENSOR_7IN1")
    private String deviceType;

    /**
     * 指标编码: sHR/sEC/sPH/CO2/NN/PP/sTMP/KK/FX/ILL/HR/FS/YX/TMP/PRS/RVC
     */
    @Schema(description = "监控指标", requiredMode = Schema.RequiredMode.REQUIRED, example = "TMP")
    private String metric;

    /**
     * 下限(可为空)
     */
    @Schema(description = "阈值下限", example = "10.0")
    private BigDecimal minValue;

    /**
     * 上限(可为空)
     */
    @Schema(description = "阈值上限", example = "40.0")
    private BigDecimal maxValue;

    /**
     * 告警级别:1提示 2一般 3严重
     */
    @Schema(description = "告警级别: 1提示 2一般 3严重", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer level;

    /**
     * 是否启用:0否 1是
     */
    @Schema(description = "是否启用: 0否 1是", example = "1")
    private Integer enabled;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
