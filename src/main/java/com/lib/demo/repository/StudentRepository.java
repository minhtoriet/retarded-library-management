package com.lib.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lib.demo.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
}
