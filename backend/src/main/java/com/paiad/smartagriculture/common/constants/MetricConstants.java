package com.paiad.smartagriculture.common.constants;

/**
 * 环境监测指标常量
 */
public final class MetricConstants {

    private MetricConstants() {
    }

    // --- 土壤传感器 ---
    /** 土壤湿度(%RH) */
    public static final String SOIL_HUMIDITY = "sHR";
    /** 土壤电导率(us/cm) */
    public static final String SOIL_EC = "sEC";
    /** 土壤pH */
    public static final String SOIL_PH = "sPH";
    /** 土壤温度(℃) */
    public static final String SOIL_TEMP = "sTMP";

    // --- 环境传感器 ---
    /** 温度(℃) */
    public static final String TEMPERATURE = "TMP";
    /** 湿度(%RH) */
    public static final String HUMIDITY = "HR";
    /** 二氧化碳(ppm) */
    public static final String CO2 = "CO2";
    /** 光照(lx) */
    public static final String ILLUMINANCE = "ILL";
    /** 大气压(kPa) */
    public static final String PRESSURE = "PRS";

    // --- 养分传感器 ---
    /** 氮(mg/kg) */
    public static final String NITROGEN = "NN";
    /** 磷(mg/kg) */
    public static final String PHOSPHORUS = "PP";
    /** 钾(mg/kg) */
    public static final String POTASSIUM = "KK";

    // --- 气象传感器 ---
    /** 风速(m/s) */
    public static final String WIND_SPEED = "FS";
    /** 风向 */
    public static final String WIND_DIR = "FX";
    /** 累积雨量(mm) */
    public static final String RAINFALL = "RVC";
    /** 雨雪(0无 1有) */
    public static final String PRECIP = "YX";
}
