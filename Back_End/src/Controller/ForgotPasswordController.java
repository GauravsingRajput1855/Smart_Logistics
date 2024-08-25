package com.couriermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.couriermanagement.service.ForgotPasswordService;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        try {
            if (forgotPasswordService.sendOtp(email)) {
                return ResponseEntity.ok(Map.of("success", true, "responseMessage", "OTP sent successfully"));
            } else {
                return ResponseEntity.status(500).body(Map.of("success", false, "responseMessage", "Failed to send OTP. Please try again."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "responseMessage", "An error occurred while sending the OTP."));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (forgotPasswordService.verifyOtp(email, otp)) {
            return ResponseEntity.ok(Map.of("success", true, "responseMessage", "OTP verified successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("success", false, "responseMessage", "Invalid OTP"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (forgotPasswordService.resetPassword(email, newPassword)) {
            return ResponseEntity.ok(Map.of("success", true, "responseMessage", "Password reset successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("success", false, "responseMessage", "Password reset failed"));
        }
    }
}
