package com.example.diary.controller;

import com.example.diary.model.User;
import com.example.diary.repository.UserRepository;
import com.example.diary.util.JwtUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public record RegisterReq(@Email String email,
                              @NotBlank String username,
                              @Size(min = 6) String password) {}
    public record LoginReq(@NotBlank String id, @NotBlank String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        if (users.findByEmail(req.email()).isPresent() || users.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 존재하는 이메일/닉네임입니다."));
        }
        User u = new User();
        u.setEmail(req.email());
        u.setUsername(req.username());
        u.setPasswordHash(encoder.encode(req.password()));
        users.save(u);

        String token = jwt.createToken(u.getId(), u.getUsername());
        return ResponseEntity.ok(Map.of("token", token,
                "user", Map.of("id", u.getId(), "username", u.getUsername())));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        var u = users.findByEmailOrUsername(req.id(), req.id()).orElse(null);
        if (u == null || !encoder.matches(req.password(), u.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("message", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
        String token = jwt.createToken(u.getId(), u.getUsername());
        return ResponseEntity.ok(Map.of("token", token,
                "user", Map.of("id", u.getId(), "username", u.getUsername())));
    }
}
