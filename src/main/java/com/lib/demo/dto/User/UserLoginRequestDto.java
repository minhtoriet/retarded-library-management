package com.lib.demo.dto.User;

import jakarta.validation.constraints.NotEmpty;

public record UserLoginRequestDto(
    @NotEmpty(message = "log req null email")
    String email, 
    @NotEmpty(message = "log req null password")
    String pasword
) {}
