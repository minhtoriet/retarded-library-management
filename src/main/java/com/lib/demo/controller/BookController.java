package com.lib.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lib.demo.dto.Book.BookDto;
import com.lib.demo.repository.BookRepository;

import org.springdoc.core.annotations.ParameterObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;


@RestController 
@RequestMapping("/api")
public class BookController {
    private final BookRepository bookRepo;
    public BookController(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    @GetMapping("/books")
    public Page<BookDto> listAllBooks(@ParameterObject @PageableDefault(size=20,sort="title",direction = Sort.Direction.ASC) Pageable pageable) {
        return bookRepo.findAll(pageable).map(b -> new BookDto(b.getBookId(), b.getTitle(), b.getAuthor(), b.getAvailableCopies()));
    }
    
}
