package org.example.service;

import org.example.dto.*;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 用户注册
    @Transactional
    public ResponseEntity<?> registerUser(RegisterDto registerDto) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>(new ErrorResponseDto("用户名已被使用"), HttpStatus.BAD_REQUEST);
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>(new ErrorResponseDto("邮箱已被注册"), HttpStatus.BAD_REQUEST);
        }

        // 创建新用户
        User user = User.builder()
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .fullName(registerDto.getFullName())
                .phone(registerDto.getPhone())
                .role("USER")
                .status("ACTIVE")
                .build();

        userRepository.save(user);
        logger.info("用户注册成功: {}", user.getUsername());

        return new ResponseEntity<>(new SuccessResponseDto("注册成功"), HttpStatus.CREATED);
    }

    // 用户登录
    public ResponseEntity<?> loginUser(LoginDto loginDto) {
        try {
            logger.info("开始用户登录验证: {}", loginDto.getUsername());

            // 验证用户是否存在
            if (!userRepository.existsByUsername(loginDto.getUsername())) {
                logger.warn("用户不存在: {}", loginDto.getUsername());
                return new ResponseEntity<>(new ErrorResponseDto("用户名或密码错误"), HttpStatus.UNAUTHORIZED);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            User user = (User) authentication.getPrincipal();
            // 更新最后登录时间
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // 构建用户响应DTO
            UserResponseDto userResponseDto = UserResponseDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phone(user.getPhone())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .createdAt(user.getCreatedAt())
                    .lastLogin(user.getLastLogin())
                    .avatarUrl(user.getAvatarUrl())
                    .build();

            JwtResponseDto jwtResponse = JwtResponseDto.builder()
                    .accessToken(jwt)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtils.getExpirationTime())
                    .user(userResponseDto)
                    .build();

            logger.info("用户登录成功: {}", user.getUsername());
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("登录过程中出现错误: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponseDto("登录失败: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 获取当前用户信息
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserResponseDto userResponseDto = buildUserResponseDto(user);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    // 根据ID获取用户信息
    public ResponseEntity<?> getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorResponseDto("用户不存在"), HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        UserResponseDto userResponseDto = buildUserResponseDto(user);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    // 获取所有用户列表
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userResponseDtos = users.stream()
                .map(this::buildUserResponseDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userResponseDtos, HttpStatus.OK);
    }

    // 更新用户信息
    @Transactional
    public ResponseEntity<?> updateUser(Long userId, UserUpdateDto updateDto) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>(new ErrorResponseDto("用户不存在"), HttpStatus.NOT_FOUND);
            }

            User user = userOptional.get();

            // 更新可修改的字段
            if (updateDto.getEmail() != null && !updateDto.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(updateDto.getEmail())) {
                    return new ResponseEntity<>(new ErrorResponseDto("邮箱已被使用"), HttpStatus.BAD_REQUEST);
                }
                user.setEmail(updateDto.getEmail());
            }

            if (updateDto.getFullName() != null) {
                user.setFullName(updateDto.getFullName());
            }

            if (updateDto.getPhone() != null) {
                user.setPhone(updateDto.getPhone());
            }

            if (updateDto.getRole() != null) {
                user.setRole(updateDto.getRole());
            }

            if (updateDto.getStatus() != null) {
                user.setStatus(updateDto.getStatus());
            }

            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            logger.info("用户信息更新成功: {}", user.getUsername());
            return new ResponseEntity<>(new SuccessResponseDto("用户信息更新成功"), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("更新用户信息失败: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponseDto("更新用户信息失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 删除用户
    @Transactional
    public ResponseEntity<?> deleteUser(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>(new ErrorResponseDto("用户不存在"), HttpStatus.NOT_FOUND);
            }

            User user = userOptional.get();

            // 防止删除自己
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            if (user.getId().equals(currentUser.getId())) {
                return new ResponseEntity<>(new ErrorResponseDto("不能删除自己的账号"), HttpStatus.BAD_REQUEST);
            }

            userRepository.delete(user);
            logger.info("用户删除成功: {}", user.getUsername());
            return new ResponseEntity<>(new SuccessResponseDto("用户删除成功"), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("删除用户失败: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponseDto("删除用户失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 禁用/启用用户
    @Transactional
    public ResponseEntity<?> toggleUserStatus(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>(new ErrorResponseDto("用户不存在"), HttpStatus.NOT_FOUND);
            }

            User user = userOptional.get();

            // 防止禁用自己
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            if (user.getId().equals(currentUser.getId())) {
                return new ResponseEntity<>(new ErrorResponseDto("不能禁用/启用自己的账号"), HttpStatus.BAD_REQUEST);
            }

            String newStatus = "ACTIVE".equals(user.getStatus()) ? "INACTIVE" : "ACTIVE";
            user.setStatus(newStatus);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            String action = "ACTIVE".equals(newStatus) ? "启用" : "禁用";
            logger.info("用户状态更新成功: {} -> {}", user.getUsername(), newStatus);
            return new ResponseEntity<>(new SuccessResponseDto(action + "用户成功"), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("更新用户状态失败: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponseDto("更新用户状态失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 辅助方法：构建用户响应DTO
    private UserResponseDto buildUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
