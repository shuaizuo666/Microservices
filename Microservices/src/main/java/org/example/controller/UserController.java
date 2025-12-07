package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "用户管理", description = "用于管理用户信息的API端点")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // 获取当前用户信息
    @GetMapping("/me")
    @Operation(
            summary = "获取当前用户信息",
            description = "获取当前登录用户的详细信息",
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未认证",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        logger.info("获取当前用户信息请求");
        return userService.getCurrentUser(authentication);
    }

    // 根据ID获取用户信息
    @GetMapping("/{id}")
    @Operation(
            summary = "根据ID获取用户信息",
            description = "根据用户ID获取指定用户的详细信息",
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未认证",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "用户不存在",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> getUserById(@PathVariable("id") Long userId) {
        logger.info("获取用户信息请求，用户ID: {}", userId);
        return userService.getUserById(userId);
    }

    // 获取所有用户列表
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "获取所有用户列表",
            description = "获取系统中所有用户的列表（仅管理员权限）",
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未认证",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "没有管理员权限",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> getAllUsers() {
        logger.info("获取所有用户列表请求");
        return userService.getAllUsers();
    }

    // 更新用户信息
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "更新用户信息",
            description = "更新指定用户的信息（仅管理员权限）",
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "更新成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "无效的请求参数",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未认证",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "没有管理员权限",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "用户不存在",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId,
                                        @Valid @RequestBody UserUpdateDto updateDto) {
        logger.info("更新用户信息请求，用户ID: {}", userId);
        return userService.updateUser(userId, updateDto);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "删除用户",
            description = "删除指定用户（仅管理员权限）",
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "删除成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未认证",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "没有管理员权限",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "用户不存在",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
        logger.info("删除用户请求，用户ID: {}", userId);
        return userService.deleteUser(userId);
    }

    // 切换用户状态（禁用/启用）
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "切换用户状态",
            description = "禁用或启用指定用户（仅管理员权限）",
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "状态切换成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未认证",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "没有管理员权限",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "用户不存在",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> toggleUserStatus(@PathVariable("id") Long userId) {
        logger.info("切换用户状态请求，用户ID: {}", userId);
        return userService.toggleUserStatus(userId);
    }
}
