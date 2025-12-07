package org.example.controller;

import org.example.dto.UserUpdateDto;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // 获取当前用户信息
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        logger.info("获取当前用户信息请求");
        return userService.getCurrentUser(authentication);
    }

    // 根据ID获取用户信息
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long userId) {
        logger.info("获取用户信息请求，用户ID: {}", userId);
        return userService.getUserById(userId);
    }

    // 获取所有用户列表
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        logger.info("获取所有用户列表请求");
        return userService.getAllUsers();
    }

    // 更新用户信息
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId,
                                        @Valid @RequestBody UserUpdateDto updateDto) {
        logger.info("更新用户信息请求，用户ID: {}", userId);
        return userService.updateUser(userId, updateDto);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
        logger.info("删除用户请求，用户ID: {}", userId);
        return userService.deleteUser(userId);
    }

    // 切换用户状态（禁用/启用）
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable("id") Long userId) {
        logger.info("切换用户状态请求，用户ID: {}", userId);
        return userService.toggleUserStatus(userId);
    }
}
