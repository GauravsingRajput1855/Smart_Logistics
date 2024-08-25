package com.couriermanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.couriermanagement.dao.UserDao;
import com.couriermanagement.entity.User;
import com.couriermanagement.resource.UserResource;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
public class ForgotPasswordService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserDao userRepository; // Assuming a repository for user data

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Map<String, String> otpStorage = new HashMap<>();

    public boolean sendOtp(String email) {
        try {
            String otp = generateOtp();
            otpStorage.put(email, otp);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + otp);

            mailSender.send(message);
            return true;
        } catch (MailException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public boolean verifyOtp(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }

    public boolean resetPassword(String email, String newPassword) {
        try {
            User user = userRepository.findByEmailId(email); // Assuming a method to find a user by email
            if (user != null) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
