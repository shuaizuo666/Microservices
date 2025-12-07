package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.LoginDto;
import org.example.dto.RegisterDto;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "用于用户注册和登录的API端点")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    // 用户注册端点
    @PostMapping("/register")
    @Operation(
            summary = "用户注册",
            description = "创建新用户账号",
            tags = {"认证管理"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "注册成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "无效的请求参数",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "用户名或邮箱已存在",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        logger.info("收到用户注册请求: {}", registerDto.getUsername());
        return userService.registerUser(registerDto);
    }

    // 用户登录端点
    @PostMapping("/login")
    @Operation(
            summary = "用户登录",
            description = "验证用户凭据并返回JWT令牌",
            tags = {"认证管理"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "登录成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "无效的请求参数",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "用户名或密码错误",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDto loginDto) {
        logger.info("收到用户登录请求: {}", loginDto.getUsername());
        return userService.loginUser(loginDto);
    }
}