package com.lib.demo.repository;

import com.lib.demo.dto.BookSearchDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lib.demo.model.Book;

public interface BookRepository extends JpaRepository<Book,Long> {
    @Query("""
        SELECT new com.lib.demo.dto.BookSearchDto(b.bookId, b.title, b.author) from Book b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%',:kw,'%'))
        OR LOWER(b.author) LIKE LOWER(CONCAT('%',:kw,'%'))
            """)
    Page<BookSearchDto> search(@Param("kw") String kw, Pageable pageable);
}
