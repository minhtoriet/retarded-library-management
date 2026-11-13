package com.lib.demo.dto;

public record BookSearchDto (
    Long bookId,
    String title,
    String author
) {}
