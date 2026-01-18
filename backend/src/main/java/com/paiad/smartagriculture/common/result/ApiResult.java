package com.paiad.smartagriculture.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一 API 返回结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResult<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    public static ApiResult<String> success() {
        return new ApiResult<>(200, "操作成功", null);
    }

    public static <T> ApiResult<T> error(String msg) {
        return new ApiResult<>(500, msg, null);
    }
}
