package com.paiad.smartagriculture.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 控制指令返回视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "控制指令返回结果")
public class ControlCommandVO {

    @Schema(description = "请求ID(追踪)")
    private String requestId;

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "指令内容")
    private String command;

    @Schema(description = "指令参数")
    private Object params;

    @Schema(description = "状态: 0=待发送, 1=已发送, 2=成功, 3=失败, 4=超时")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusText;

    @Schema(description = "发送时间")
    private LocalDateTime sentAt;

    /**
     * 根据状态码获取状态描述
     */
    public static String getStatusText(Integer status) {
        if (status == null)
            return "未知";
        return switch (status) {
            case 0 -> "待发送";
            case 1 -> "已发送";
            case 2 -> "成功";
            case 3 -> "失败";
            case 4 -> "超时";
            default -> "未知";
        };
    }
}
