package com.lib.demo.dto.User;

import jakarta.validation.constraints.NotEmpty;

public record UserRegisterRequestDto(
    @NotEmpty(message = "reg req null email")
    String email, 
    @NotEmpty(message = "reg req null password")
    String password
) {}
