package com.bank.pfe1.controller;

import com.bank.pfe1.dto.LoginRequest;
import com.bank.pfe1.dto.LoginResponse;
import com.bank.pfe1.entity.User;
import com.bank.pfe1.repository.UserRepository;
import com.bank.pfe1.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // 1. Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // 2. Check if password is correct
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Wrong password!");
        }

        // 3. Check if user is active
        if (!user.isActive()) {
            return ResponseEntity.badRequest().body("Account is disabled!");
        }

        // 4. Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        // 5. Return token + user info
        return ResponseEntity.ok(new LoginResponse(
                token,
                user.getUsername(),
                user.getRole().name()
        ));
    }
}
