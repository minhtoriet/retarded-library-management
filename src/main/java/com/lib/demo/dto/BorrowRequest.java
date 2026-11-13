package com.lib.demo.dto;

import jakarta.validation.constraints.NotEmpty;

public record BorrowRequest(
    @NotEmpty(message = "brw req null userid")
    Long userId, 
    @NotEmpty(message = "brw req null bookid")
    Long bookId
) {}