package com.lib.demo.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity 
@Getter @Setter @NoArgsConstructor // <--- THIS is it
@Table(name="books")
public class Book {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="book_id")
    private @Setter(AccessLevel.PRIVATE) Long bookId;

    @Column(name="title",length=200,nullable = false)
    private String title;

    @Column(name="author",length=100,nullable = false)
    private String author;

    @Column(name="available_copies",nullable = false)
    private int availableCopies;

    @Column(name="borrow_count")
    private int borrowCounts;
    
}
