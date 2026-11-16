package com.lib.demo.dto;

import jakarta.validation.constraints.NotNull;

public record BorrowRequest(
    @NotNull(message = "brw req null userid")
    Long userId, 
    @NotNull(message = "brw req null bookid")
    Long bookId
) {}