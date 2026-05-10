package com.mailtrack.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity(name = "customers")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String picture;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    @PrePersist
    public void onCreate() {
        this.createdAt    = LocalDateTime.now();
        this.lastLoginAt  = LocalDateTime.now();
    }
}