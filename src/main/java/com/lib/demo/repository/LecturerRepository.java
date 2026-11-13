package com.lib.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lib.demo.model.Lecturer;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    
}