package com.sonnhuynhh.auraplay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    // Dạy cho Sping Boot biết cách tạo ra PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Trả về thuật toán mã hóa BCrypt
        return new BCryptPasswordEncoder();
    }
}
