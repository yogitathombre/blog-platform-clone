package com.example.clonemedium.service;

import com.example.clonemedium.dto.SignInRequest;
import com.example.clonemedium.dto.SignUpRequest;
import com.example.clonemedium.model.User;
import com.example.clonemedium.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User signInWithEmail(SignInRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User signInWithSocial(SignInRequest request) {
        User.AuthProvider provider = User.AuthProvider.valueOf(request.getAuthProvider());
        // Check if user exists with this provider
        Optional<User> userOpt = userRepository.findByProviderIdAndAuthProvider(
            request.getProviderId(),
            provider
        );
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            return userRepository.save(user);
        }
        // Check if user exists with this email
        userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Link the social account to existing user
            user.setProviderId(request.getProviderId());
            user.setAuthProvider(provider);
            user.setLastLogin(LocalDateTime.now());
            return userRepository.save(user);
        }
        // Create new user
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setProviderId(request.getProviderId());
        newUser.setAuthProvider(provider);
        newUser.setLastLogin(LocalDateTime.now());
        newUser.setEmailVerified(true); // Social login emails are typically verified
        return userRepository.save(newUser);
    }

    public User signUp(SignUpRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthProvider(User.AuthProvider.valueOf(request.getAuthProvider()));
        user.setEnabled(true);
        // Save user
        return userRepository.save(user);
    }
}
























       

       
       

       

       

       

       

       
