package com.example.clonemedium.config;

import com.example.clonemedium.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final UserService userService;

    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeRequests(auth -> auth
                .antMatchers("/", "/login", "/signin", "/api/auth/**", "/h2-console/**", 
                           "/oauth2/**", "/login/oauth2/**", "/error", "/signup", "/signup/email", "/welcome", "/new-story").permitAll()
                .antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/signin")
                .userInfoEndpoint()
                    .userService(oauth2UserService())
                .and()
                .successHandler((request, response, authentication) -> {
                    logger.debug("OAuth2 authentication successful. Authentication: {}", authentication);
                    if (authentication != null && authentication.isAuthenticated()) {
                        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                        logger.debug("OAuth2User attributes: {}", oauth2User.getAttributes());
                        // Redirect to welcome page after successful authentication
                        response.sendRedirect("/welcome");
                    } else {
                        response.sendRedirect("/signin?error=authentication_failed");
                    }
                })
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                .and()
                .redirectionEndpoint()
                    .baseUri("/oauth2/callback/*")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .headers(headers -> headers.frameOptions().disable());
        
        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return (userRequest) -> {
            logger.debug("Processing OAuth2 user request for registration: {}", userRequest.getClientRegistration().getRegistrationId());
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            logger.debug("OAuth2User loaded: {}", oauth2User);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            return userService.processOAuthPostLogin(oauth2User, registrationId);
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 