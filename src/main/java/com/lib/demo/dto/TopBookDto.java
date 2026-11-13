package com.lib.demo.dto;

public record TopBookDto(
    Long bookId, String title, Long borrowCount
) {}
