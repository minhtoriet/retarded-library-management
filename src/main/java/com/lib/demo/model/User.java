package com.lib.demo.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter @Setter @NoArgsConstructor // <--- THIS is it
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="user_id")
    private @Setter(AccessLevel.PRIVATE) Long userId;

    @Column (name="full_name", length=100, nullable=false)
    private String fullName;

    @Column(name="email", length=150, nullable=false, unique=true)
    private String email;
}
