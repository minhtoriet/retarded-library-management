package com.lib.demo.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter @Setter @NoArgsConstructor // <--- THIS is it
@Table(name="students")
public class Student extends User{
    @Column(name="student_code",length = 10,nullable = false,unique = true)
    private String studentCode;

    @Column(name="major",length = 30,nullable = false)
    private String major;
}
