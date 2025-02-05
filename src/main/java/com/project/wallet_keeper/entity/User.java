package com.project.wallet_keeper.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"user\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "provider")
    private String provider;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Builder
    public User(String email, String password, String nickname, LocalDate birth, Role role, String provider) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birth = birth;
        this.role = role;
        this.provider = provider;
    }

    @PrePersist // 영속화 될 때의 시간을 설정하기 위해 사용
    public void prePersist() {
        this.createdAt = LocalDateTime.now().withNano(0);
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    public User update(String nickname, LocalDate birth) {
        this.nickname = nickname;
        this.birth = birth;
        return this;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void deleteUser() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now().withNano(0);
    }
}
