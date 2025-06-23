package com.example.clonemedium.service;

import com.example.clonemedium.model.User;
import com.example.clonemedium.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    public OAuth2User processOAuthPostLogin(OAuth2User oauth2User, String registrationId) {
        logger.debug("Processing OAuth2 post login for provider: {}", registrationId);
        logger.debug("OAuth2User attributes: {}", oauth2User.getAttributes());

        String email = oauth2User.getAttribute("email");
        String providerId = oauth2User.getAttribute("id");
        String name = oauth2User.getAttribute("name");

        logger.debug("Extracted user info - Email: {}, ProviderId: {}, Name: {}", email, providerId, name);

        if (email == null) {
            logger.error("Email is null in OAuth2User attributes");
            throw new UsernameNotFoundException("Email not found in OAuth2User attributes");
        }

        User.AuthProvider provider = User.AuthProvider.valueOf(registrationId.toUpperCase());

        // Check if user exists with this provider
        User user = userRepository.findByProviderIdAndAuthProvider(providerId, provider)
            .orElseGet(() -> {
                logger.debug("User not found with provider, checking by email");
                // Check if user exists with this email
                return userRepository.findByEmail(email)
                    .map(existingUser -> {
                        logger.debug("Found existing user by email, linking provider");
                        existingUser.setProviderId(providerId);
                        existingUser.setAuthProvider(provider);
                        if (existingUser.getName() == null || existingUser.getName().isEmpty()) {
                            existingUser.setName(name);
                        }
                        return existingUser;
                    })
                    .orElseGet(() -> {
                        logger.debug("Creating new user");
                        User newUser = new User();
                        newUser.setName(name);
                        newUser.setEmail(email);
                        newUser.setProviderId(providerId);
                        newUser.setAuthProvider(provider);
                        newUser.setEmailVerified(true);
                        return newUser;
                    });
            });

        user.setLastLogin(java.time.LocalDateTime.now());
        user = userRepository.save(user);
        logger.debug("Saved user: {}", user);

        // Store user info in OAuth2User attributes
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put("userId", user.getId());
        
        return new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "id"
        );
    }
} 