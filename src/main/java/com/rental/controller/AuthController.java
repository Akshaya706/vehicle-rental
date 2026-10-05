package com.rental.controller;

import com.rental.dto.LoginRequest;
import com.rental.dto.LoginResponse;
import com.rental.dto.RegisterCustomerRequest;
import com.rental.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterCustomerRequest request) {
        LoginResponse response = authService.registerCustomer(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String oldPassword = body.get("oldPassword").toString();
        String newPassword = body.get("newPassword").toString();

        boolean result = authService.changePassword(userId, oldPassword, newPassword);
        if (result) {
            return ResponseEntity.ok(Map.of("message", "Password changed successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid old password", "success", false));
    }
}
