package com.lib.demo.controller;

import com.lib.demo.exception.ApiExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lib.demo.dto.BorrowHistoryDto;
import com.lib.demo.dto.BorrowRequest;
import com.lib.demo.dto.BorrowResponse;
import com.lib.demo.service.BorrowService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api")
public class BorrowController {
    private final ApiExceptionHandler apiExceptionHandler;
    private final BorrowService borrowService;
    public BorrowController (BorrowService service, ApiExceptionHandler apiExceptionHandler){
        borrowService = service;
        this.apiExceptionHandler = apiExceptionHandler;
    }

    @PostMapping("/borrow")
    public ResponseEntity<BorrowResponse> borrow(@RequestBody @Valid BorrowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowService.borrow(request));
    }
    
    @GetMapping("/user/{userId}/borrows")
    public ResponseEntity<Page<BorrowHistoryDto>> history(@PathVariable Long userId, @PathVariable Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.historyForUser(userId, pageable));
    }

    @GetMapping("/return")
    public ResponseEntity<String> returnBook(@RequestBody BorrowRequest request) {
        try {
            borrowService.returnBook(request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("wrong ass id");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("return success");
    }
    
}
