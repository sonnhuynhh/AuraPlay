package com.sonnhuynhh.auraplay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration // Đánh dấu đây là file cấu hình của Spring
@EnableWebSecurity // Bật tính năng tùy chỉnh Security
@RequiredArgsConstructor // Tự động tạo Constructer để Inject JwtAuthenticationFilter vào
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Cấu hình phân quyền truy cập các API
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Tắt bảo vệ CSRF (thường tắt khi làm REST API dùng Token)
        httpSecurity.csrf(csrf -> csrf.disable())
                    // Cấu hình đường dẫn nào bị khóa, đường dẫn nào được thả
                    .authorizeHttpRequests(request -> request
                        // Cho phép gọi API đăng ký (POST /users) và đăng nhập (POST /auth/login) mà không cần đăng nhập
                        .requestMatchers(HttpMethod.POST, "/users", "/auth/login").permitAll()
                        // Bất kỳ API nào khác đều phải đăng nhập
                        .anyRequest().authenticated())
                        // Thêm JwtAuthenticationFilter vào chuỗi Filter của Spring Security
                        // Nó sẽ chạy trước UsernamePasswordAuthenticationFilter
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return httpSecurity.build();
    }
}
