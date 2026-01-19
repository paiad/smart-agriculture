package com.paiad.smartagriculture.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备信息实体类
 * 用于存储设备的唯一标识、类型、名称、状态等基础信息
 */
@Data
@TableName("device")
public class Device implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备业务唯一ID(来自硬件/网关)
     */
    private String deviceId;

    /**
     * 设备类型:SENSOR_7IN1/WEATHER_STATION/FAN/PUMP等
     */
    private String deviceType;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 所属大棚/温室ID
     */
    private Long greenhouseId;

    /**
     * 位置描述
     */
    private String locationDesc;

    /**
     * 状态:0停用 1启用 2故障 3维护
     */
    private Integer status;

    /**
     * 在线快照:0离线 1在线
     */
    private Integer online;

    /**
     * 最后心跳/最后在线时间
     */
    private LocalDateTime lastSeenAt;

    /**
     * 最后数据上报时间
     */
    private LocalDateTime lastDataAt;

    /**
     * 厂商
     */
    private String vendor;

    /**
     * 型号
     */
    private String model;

    /**
     * 固件版本
     */
    private String fwVersion;

    /**
     * 协议:MQTT/HTTP等
     */
    private String protocol;

    /**
     * 能力描述(JSON):测量项/控制指令
     */
    private String capabilities;

    /**
     * 备注
     */
    private String remark;

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

    /**
     * 逻辑删除:0否 1是
     */
    @TableLogic
    private Integer deleted;
}
