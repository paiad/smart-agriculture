package com.paiad.smartagriculture.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备控制指令实体类
 * 用于存储向设备下发的控制指令及其执行状态（待发送、已发送、成功、失败等）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("control_command")
@Schema(description = "设备控制指令")
public class ControlCommand implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", hidden = true)
    private Long id;

    /**
     * 目标设备ID(关联 device.device_id)
     */
    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "SENSOR001")
    private String deviceId;

    /**
     * 指令:FAN_ON/FAN_OFF/PUMP_ON/SET_SPEED等
     */
    @Schema(description = "指令内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "FAN_ON")
    private String command;

    /**
     * 指令参数(JSON)
     */
    @Schema(description = "指令参数(JSON)", example = "{\"speed\": 80}")
    private String params;

    /**
     * 状态:0待发送 1已发送 2成功 3失败 4超时
     */
    @Schema(description = "状态: 0=待发送, 1=已发送, 2=成功, 3=失败, 4=超时", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer status;

    /**
     * 请求ID(幂等/追踪)
     */
    @Schema(description = "请求ID(追踪)", accessMode = Schema.AccessMode.READ_ONLY)
    private String requestId;

    /**
     * 失败原因
     */
    @Schema(description = "失败原因", accessMode = Schema.AccessMode.READ_ONLY)
    private String errorMsg;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", hidden = true)
    private LocalDateTime createdAt;

    /**
     * 发送时间
     */
    @Schema(description = "发送时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime sentAt;

    /**
     * 设备确认时间
     */
    @Schema(description = "确认时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime ackAt;
}
