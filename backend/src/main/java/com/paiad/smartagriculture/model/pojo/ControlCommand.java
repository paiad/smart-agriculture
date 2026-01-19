package com.paiad.smartagriculture.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备控制指令实体类
 * 用于存储向设备下发的控制指令及其执行状态（待发送、已发送、成功、失败等）
 */
@Data
@TableName("control_command")
public class ControlCommand implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 目标设备ID(关联 device.device_id)
     */
    private String deviceId;

    /**
     * 指令:FAN_ON/FAN_OFF/PUMP_ON/SET_SPEED等
     */
    private String command;

    /**
     * 指令参数(JSON)
     */
    private String params;

    /**
     * 状态:0待发送 1已发送 2成功 3失败
     */
    private Integer status;

    /**
     * 请求ID(幂等/追踪)
     */
    private String requestId;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 发送时间
     */
    private LocalDateTime sentAt;

    /**
     * 设备确认时间
     */
    private LocalDateTime ackAt;
}
