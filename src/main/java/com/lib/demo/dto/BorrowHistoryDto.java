package com.lib.demo.dto;

import java.time.LocalDate;

public record BorrowHistoryDto (
    Long borrowId, String title, LocalDate borrowDate, LocalDate returnDate
){}
