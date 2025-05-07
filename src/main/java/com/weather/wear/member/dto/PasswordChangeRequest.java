package com.weather.wear.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeRequest {

    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해야 합니다.")
    private String newPassword;
}