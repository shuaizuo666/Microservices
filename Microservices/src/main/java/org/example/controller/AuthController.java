package org.example.controller;

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
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    // 用户注册端点
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        logger.info("收到用户注册请求: {}", registerDto.getUsername());
        return userService.registerUser(registerDto);
    }

    // 用户登录端点
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDto loginDto) {
        logger.info("收到用户登录请求: {}", loginDto.getUsername());
        return userService.loginUser(loginDto);
    }
}