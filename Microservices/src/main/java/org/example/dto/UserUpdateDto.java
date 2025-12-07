package org.example.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UserUpdateDto {
    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String fullName;

    private String phone;

    private String role;

    private String status;
}
