package com.lib.demo.service;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import com.lib.demo.model.Book;
import com.lib.demo.model.BorrowRecord;
import com.lib.demo.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lib.demo.dto.BorrowHistoryDto;
import com.lib.demo.dto.BorrowRequest;
import com.lib.demo.dto.BorrowResponse;
import com.lib.demo.repository.BookRepository;
import com.lib.demo.repository.BorrowRecordRepository;
import com.lib.demo.repository.UserRepository;

@Service
public class BorrowService {
    private final BorrowRecordRepository borrowRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    public BorrowService(BorrowRecordRepository borrowRepo, BookRepository bookRepo,
            UserRepository userRepo) {
        this.borrowRepo = borrowRepo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public BorrowResponse borrow(BorrowRequest request) {
        User user = userRepo.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("user " + request.userId() + " not found"));
        Book book = bookRepo.findById(request.bookId())
                .orElseThrow(() -> new NoSuchElementException("book " + request.userId() + " not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("no copies available");
        }
        if (borrowRepo.existsByUser_UserIdAndBook_BookIdAndReturnDateIsNull(request.userId(), request.bookId())) {
            throw new IllegalStateException("user alr borrowed ts book");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setBorrowCounts(book.getBorrowCounts() + 1);

        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setUser(user);
        record.setBorrowDate(LocalDate.now());

        return BorrowResponse.from(borrowRepo.save(record));
    }

    @Transactional(readOnly = true)
    public Page<BorrowHistoryDto> historyForUser(Long userId, Pageable pageable) {
        return borrowRepo.findBorrowHistoryByUserId(userId, pageable);
    }

    @Transactional
    public void returnBook(BorrowRequest request){
        User user = userRepo.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("user " + request.userId() + " not found"));
        Book book = bookRepo.findById(request.bookId())
                .orElseThrow(() -> new NoSuchElementException("book " + request.userId() + " not found"));
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        

    }
}
