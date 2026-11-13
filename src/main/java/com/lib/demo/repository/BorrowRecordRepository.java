package com.lib.demo.repository;

import com.lib.demo.dto.BorrowHistoryDto;
import com.lib.demo.dto.TopBookDto;
import com.lib.demo.model.BorrowRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    @Query("""
            SELECT new com.lib.demo.dto.BorrowHistoryDto(b.bookId, b.title, br.borrowDate, br.returnDate)
            FROM BorrowRecord br JOIN br.book b
            WHERE br.user.userId = :userId
            ORDER BY br.borrowDate DESC
            """)
    Page<BorrowHistoryDto> findBorrowHistoryByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = """
            SELECT new com.lib.demo.dto.TopBookDto(b.bookId, b.title, COUNT(br))
            FROM BorrowRecord br join br.book b
            GROUP BY b.bookId, b.title
            ORDER BY COUNT(br) DESC
            """, countQuery = "SELECT COUNT (DISTINCT br.book.bookId) FROM BorrowRecord br")
    Page<TopBookDto> findTopBorrowedBook(Pageable pageable);

    // is this user currently holding this book?
    boolean existsByUser_UserIdAndBook_BookIdAndReturnDateIsNull(Long userId, Long bookId);

    // find by userId and bookId and returnDate is null
    
}
