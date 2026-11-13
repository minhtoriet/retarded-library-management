package com.lib.demo.model;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter @Setter @NoArgsConstructor // <--- THIS is it
@Table(name="borrow_records")
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private @Setter(AccessLevel.PRIVATE) Long borrowId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name="borrow_date",nullable = false)
    private LocalDate borrowDate;

    @Column(name="return_date")
    private LocalDate returnDate;
}
