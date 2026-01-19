package com.paiad.smartagriculture.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("env_data")
public class EnvData implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联 device.device_id
     */
    private String deviceId;

    /**
     * 采集时间
     */
    private LocalDateTime ts;

    /**
     * sHR 土壤湿度(%RH)
     */
    private BigDecimal soilHumidity;

    /**
     * sEC 土壤电导率(us/cm)
     */
    private BigDecimal soilEc;

    /**
     * sPH 土壤pH
     */
    private BigDecimal soilPh;

    /**
     * CO2 二氧化碳(ppm)
     */
    private BigDecimal co2;

    /**
     * NN 氮(mg/kg)
     */
    private BigDecimal nitrogen;

    /**
     * PP 磷(mg/kg)
     */
    private BigDecimal phosphorus;

    /**
     * sTMP 土壤温度(℃)
     */
    private BigDecimal soilTemp;

    /**
     * KK 钾(mg/kg)
     */
    private BigDecimal potassium;

    /**
     * FX 风向(N/NE/E...)
     */
    private String windDir;

    /**
     * ILL 光照(lx)
     */
    private BigDecimal illuminance;

    /**
     * HR 湿度(%RH)
     */
    private BigDecimal humidity;

    /**
     * FS 风速(m/s)
     */
    private BigDecimal windSpeed;

    /**
     * YX 雨雪(0无 1有)
     */
    private Integer precip;

    /**
     * TMP 温度(℃)
     */
    private BigDecimal temperature;

    /**
     * PRS 大气压(kPa)
     */
    private BigDecimal pressure;

    /**
     * RVC 累积雨量(mm)
     */
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
