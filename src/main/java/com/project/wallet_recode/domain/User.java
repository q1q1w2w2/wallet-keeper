package com.project.wallet_recode.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "age")
    private int age;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @Builder
    public User(String email, String password, String username, int age) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.age = age;
        this.createdAt = LocalDateTime.now().withNano(0);
        this.updatedAt = LocalDateTime.now().withNano(0);
    }
}
