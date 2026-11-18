package com.lib.demo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lib.demo.model.Book;
import com.lib.demo.model.Department;
import com.lib.demo.model.Lecturer;
import com.lib.demo.model.Student;
import com.lib.demo.model.User;
import com.lib.demo.repository.BookRepository;
import com.lib.demo.repository.UserRepository;

@Component 
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepo;
    private final BookRepository bookRepo;

    public DataSeeder(UserRepository userRepo, BookRepository bookRepo) {
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
    }

    @Override 
    @Transactional 
    public void run(String... args){
        if (userRepo.count() > 0 || bookRepo.count() > 0) {
            return;
        }

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 2000; i++) {
            Student s = new Student();
            s.setFullName("Student "+i);
            s.setEmail("student"+i+"@example.com");
            s.setStudentCode(String.format("STU%04d", i));
            //s.setStudentCode("STU" + (i < 10 ? "000" : i < 100 ? "00" : i < 1000 ? "0" : "") + i);
            s.setMajor("SWE");
            users.add(s);
        }
        for (int i = 1; i <= 300; i++) {
            Lecturer l = new Lecturer();
            l.setFullName("Student "+i);
            l.setEmail("lecturer"+i+"@example.com");
            l.setLecturerCode(String.format("LEC%04d", i));
            l.setDepartment(Department.CIT);
            users.add(l);
        }
        userRepo.saveAll(users);

        List<Book> books = new ArrayList<>();
        for (int i = 1; i <= 2300; i++) {
            Book b = new Book();
            b.setTitle("Book "+i);
            b.setAuthor("Author "+i);
            b.setAvailableCopies(1 + (i % 5));
            b.setBorrowCounts(0);
            books.add(b);
        }
        bookRepo.saveAll(books);
    }
}
