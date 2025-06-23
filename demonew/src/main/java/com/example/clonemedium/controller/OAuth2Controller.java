package com.example.clonemedium.controller;

import com.example.clonemedium.model.User;
import com.example.clonemedium.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);
    private final UserService userService;

    public OAuth2Controller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        logger.debug("OAuth2 login endpoint called");
        return "redirect:/oauth2/authorize/facebook";
    }
    
    @GetMapping("/callback/facebook")
    public ResponseEntity<?> facebookCallback(Authentication authentication) {
        logger.debug("Facebook callback received. Authentication: {}", authentication);
        
        if (authentication == null) {
            logger.warn("Authentication is null");
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Authentication failed - No authentication information received"
            ));
        }

        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            logger.warn("Authentication is not OAuth2AuthenticationToken: {}", authentication.getClass());
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Authentication failed - Invalid authentication type"
            ));
        }

        OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauth2Auth.getPrincipal();
        
        logger.debug("OAuth2User: {}", oauth2User);
        logger.debug("OAuth2User attributes: {}", oauth2User != null ? oauth2User.getAttributes() : "null");
        
        if (oauth2User == null) {
            logger.warn("OAuth2User is null");
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Authentication failed - No user information received"
            ));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Authentication successful");
        response.put("user", oauth2User.getAttributes());
        
        return ResponseEntity.ok(response);
    }
} 