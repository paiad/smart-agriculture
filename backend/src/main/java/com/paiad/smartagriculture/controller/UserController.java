package com.paiad.smartagriculture.controller;

import com.paiad.smartagriculture.common.result.ApiResult;
import com.paiad.smartagriculture.model.dto.LoginDTO;
import com.paiad.smartagriculture.model.vo.UserVO;
import com.paiad.smartagriculture.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "用户接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResult<String> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return ApiResult.success(token);
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public ApiResult<UserVO> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO user = userService.getCurrentUser(token);
        return ApiResult.success(user);
    }
}
