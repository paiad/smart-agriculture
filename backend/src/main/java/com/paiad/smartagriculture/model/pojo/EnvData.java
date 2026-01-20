package com.paiad.smartagriculture.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 环境数据实体类
 * 用于存储传感器上报的环境监测数据（如温度、湿度、PH值等）
 */
@Data
@TableName("env_data")
@Schema(description = "环境监测数据")
public class EnvData implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", hidden = true)
    private Long id;

    /**
     * 关联 device.device_id
     */
    @Schema(description = "设备ID", example = "SENSOR001")
    private String deviceId;

    /**
     * 采集时间
     */
    @Schema(description = "采集时间")
    private LocalDateTime ts;

    /**
     * sHR 土壤湿度(%RH)
     */
    @Schema(description = "土壤湿度 %RH", example = "45.2")
    private BigDecimal soilHumidity;

    /**
     * sEC 土壤电导率(us/cm)
     */
    @Schema(description = "土壤电导率 us/cm", example = "1.2")
    private BigDecimal soilEc;

    /**
     * sPH 土壤pH
     */
    @Schema(description = "土壤pH", example = "6.5")
    private BigDecimal soilPh;

    /**
     * CO2 二氧化碳(ppm)
     */
    @Schema(description = "二氧化碳浓 ppm", example = "420")
    private BigDecimal co2;

    /**
     * NN 氮(mg/kg)
     */
    @Schema(description = "氮 mg/kg", example = "120")
    private BigDecimal nitrogen;

    /**
     * PP 磷(mg/kg)
     */
    @Schema(description = "磷 mg/kg", example = "50")
    private BigDecimal phosphorus;

    /**
     * sTMP 土壤温度(℃)
     */
    @Schema(description = "土壤温度 ℃", example = "24.5")
    private BigDecimal soilTemp;

    /**
     * KK 钾(mg/kg)
     */
    @Schema(description = "钾 mg/kg", example = "200")
    private BigDecimal potassium;

    /**
     * FX 风向(N/NE/E...)
     */
    @Schema(description = "风向 (N/NE/E...)", example = "SE")
    private String windDir;

    /**
     * ILL 光照(lx)
     */
    @Schema(description = "光照强度 lx", example = "15000")
    private BigDecimal illuminance;

    /**
     * HR 湿度(%RH)
     */
    @Schema(description = "空气湿度 %RH", example = "60.0")
    private BigDecimal humidity;

    /**
     * FS 风速(m/s)
     */
    @Schema(description = "风速 m/s", example = "3.5")
    private BigDecimal windSpeed;

    /**
     * YX 雨雪(0无 1有)
     */
    @Schema(description = "雨雪状态 (0无 1有)", example = "0")
    private Integer precip;

    /**
     * TMP 温度(℃)
     */
    @Schema(description = "空气温度 ℃", example = "26.5")
    private BigDecimal temperature;

    /**
     * PRS 大气压(kPa)
     */
    @Schema(description = "大气压 kPa", example = "101.3")
    private BigDecimal pressure;

    /**
     * RVC 累积雨量(mm)
     */
    @Schema(description = "累积雨量 mm", example = "0.5")
    private BigDecimal rainfall;

    /**
     * 原始上报(排查用)
     */
    private String rawPayload;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
