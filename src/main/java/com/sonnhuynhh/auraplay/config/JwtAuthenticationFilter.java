package com.sonnhuynhh.auraplay.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sonnhuynhh.auraplay.service.AuthenticationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
// Kế thừa OncePerRequestFilter đảm bảo mỗi Request người dùng gửi lên 
// chỉ bị chặn lại hỏi thăm ĐÚNG 1 LẦN.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
        // Lấy token từ request
        String jwtToken = extractTokenFromHeader(request);
        
        // Kiểm tra xem token có hợp lệ không
        if (StringUtils.hasText(jwtToken) && authenticationService.introspect(jwtToken)) {
            // Trích xuất username từ Token (setSubject lúc tạo Token)
            String username = authenticationService.extractUsername(jwtToken);
            
            // Tạo ra một "bản sao" của token để đưa vào Security Context
            // (Lưu ý: Ở đây ta chưa có User cụ thể, nên tạm để null)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, java.util.Collections.emptyList());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Chuyển request sang Filter tiếp theo trong chuỗi
        filterChain.doFilter(request, response);
    }

    // Hàm phụ để lấy token từ header "Authorization: Bearer <Token>"
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Dùng substring để cắt bỏ chữ "Bearer " (7 ký tự)
            return bearerToken.substring(7);
        }
        return null;
    }
}
