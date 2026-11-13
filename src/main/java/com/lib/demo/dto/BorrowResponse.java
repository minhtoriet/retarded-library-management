package com.lib.demo.dto;

import java.time.LocalDate;

import com.lib.demo.model.BorrowRecord;

public record BorrowResponse(Long borrowId, Long userId, Long bookId,
                             String title, LocalDate borrowDate, LocalDate returnDate) {

    public static BorrowResponse from(BorrowRecord r) {
        return new BorrowResponse(
            r.getBorrowId(), r.getUser().getUserId(), r.getBook().getBookId(),
            r.getBook().getTitle(), r.getBorrowDate(), r.getReturnDate()
        );
    }
}