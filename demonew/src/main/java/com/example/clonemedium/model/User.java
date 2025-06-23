package com.example.clonemedium.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true) // Nullable because social login users might not have a password
    private String password;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(name = "provider_id") // ID from the social provider (Google, Facebook, etc.)
    private String providerId;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified = false;

    @Column(name = "is_phone_verified")
    private boolean isPhoneVerified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "enabled")
    private boolean enabled = false;

    @ManyToMany
    @JoinTable(
        name = "user_followed_topics",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private Set<Topic> followedTopics = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AuthProvider {
        LOCAL,    // Email sign-in
        GOOGLE,
        FACEBOOK,
        APPLE,
        X
    }
} 