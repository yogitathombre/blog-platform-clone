package com.example.clonemedium.controller;
import com.example.clonemedium.dto.SignInRequest;
import com.example.clonemedium.dto.SignUpRequest;
import com.example.clonemedium.service.AuthService;
import com.example.clonemedium.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/signup")
    public String showSignUpPage() {
        return "signup-modal";
    }

    @GetMapping("/signup/email")
    public String showSignUpEmailPage() {
        return "signup-email";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/signin";
    }
 
    @GetMapping("/signin")
    public String showSignInPage() {
        return "signin";
    }


    @PostMapping("/signin")
    public String signInWithEmail(@RequestParam String email, 
                                @RequestParam String password,
                                RedirectAttributes redirectAttributes) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Get user details
            SignInRequest request = new SignInRequest();
            request.setEmail(email);
            request.setPassword(password);
            authService.signInWithEmail(request);
            
            return "redirect:/welcome";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signin?error=true";
        }
    }

    @PostMapping("/api/auth/social")
    @ResponseBody
    public ResponseEntity<?> signInWithSocial(@RequestBody SignInRequest request) {
        try {
            User user = authService.signInWithSocial(request);
            return ResponseEntity.ok(createSuccessResponse(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/api/auth/signup")
    @ResponseBody
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request) {
        try {
            User user = authService.signUp(request);
            return ResponseEntity.ok(createSuccessResponse(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", user);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
} 