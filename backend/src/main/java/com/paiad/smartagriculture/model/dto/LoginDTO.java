package com.paiad.smartagriculture.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginDTO {
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
