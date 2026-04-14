package com.sonnhuynhh.auraplay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Đánh dấu đây là file cấu hình của Spring
@EnableWebSecurity // Bật tính năng tùy chỉnh Security
public class SecurityConfig {

    // Dạy cho Sping Bôt biết cách tạo ra PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Trả về thuật toán mã hóa BCrypt
        return new BCryptPasswordEncoder();
    }

    // Cấu hình phân quyền truy cập các API
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable()) // Tắt bảo vệ CSRF (thường tắt khi làm REST API dùng Token)
                    .authorizeHttpRequests(request -> request // Cấu hình đường dẫn nào bị khóa, đường dẫn nào được thả
                        .requestMatchers(HttpMethod.POST, "/users", "/auth/login").permitAll() // Cho phép gọi API đăng ký (POST /users) và đăng nhập (POST /auth/login) mà không cần đăng nhập
                        .anyRequest().authenticated()); // Bất kỳ API nào khác đều phải đăng nhập

        return httpSecurity.build();
    }
}
