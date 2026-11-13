package com.lib.demo.model;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter @Setter @NoArgsConstructor // <--- THIS is it
@Table(name="lecturers")
public class Lecturer extends User{
    @Column(name="lecturer_code",length = 10,nullable = false,unique = true)
    private String lecturerCode;

    @Column(name="department",length = 30,nullable = false)
    @Enumerated(EnumType.STRING)
    private Department department;
}

enum Department {
    CIT, BBS, SET, SNS
}