package com.paiad.smartagriculture.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 告警规则实体类
 * 用于定义数据异常的判断标准（监测指标、阈值范围等）
 */
@Data
@TableName("alarm_rule")
public class AlarmRule implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备ID(优先级最高，非空表示只对该设备生效)
     */
    private String deviceId;

    /**
     * 设备类型(次优先级，device_id为空时可用)
     */
    private String deviceType;

    /**
     * 指标编码: sHR/sEC/sPH/CO2/NN/PP/sTMP/KK/FX/ILL/HR/FS/YX/TMP/PRS/RVC
     */
    private String metric;

    /**
     * 下限(可为空)
     */
    private BigDecimal minValue;

    /**
     * 上限(可为空)
     */
    private BigDecimal maxValue;

    /**
     * 告警级别:1提示 2一般 3严重
     */
    private Integer level;

    /**
     * 是否启用:0否 1是
     */
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
