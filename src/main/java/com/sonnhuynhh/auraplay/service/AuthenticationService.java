package com.sonnhuynhh.auraplay.service;

import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.sonnhuynhh.auraplay.dto.request.AuthenticationRequest;
import com.sonnhuynhh.auraplay.dto.response.AuthenticationResponse;
import com.sonnhuynhh.auraplay.entity.User;
import com.sonnhuynhh.auraplay.exception.AppException;
import com.sonnhuynhh.auraplay.exception.ErrorCode;
import com.sonnhuynhh.auraplay.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Lấy chữ ký từ file application.properties truyền vào biến này
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Tìm user theo username
        User user = userRepository.findByUsername(request.getUsername())
                                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Dùng BCrypt để check xem mật khẩu gõ vào có khớp với dải băm trong DB không
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 3. Nếu mọi thứ đúng, gọi hàm tạo token
        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    // Hàm chế tạo token
    private String generateToken(User user) {

        // Cấu hình thời gian sống của token là 1 tiếng (Hết 1 tiếng user phải đăng nhập lại)
        Date issueTime = new Date();
        Date expiryTime = new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli());

        return Jwts.builder()
                .setSubject(user.getUsername()) // Chủ sở hữu token
                .setIssuer("auraplay.com") // Người phát hành token
                .setIssuedAt(issueTime) // Thời điểm phát hành token
                .setExpiration(expiryTime) // Thời điểm hết hạn token
                // Dùng SIGNER_KEY làm con dấu để ký vào token
                .signWith(Keys.hmacShaKeyFor(SIGNER_KEY.getBytes()), SignatureAlgorithm.HS512)
                .compact(); // Bấm nút "In thẻ", token sẽ được in ra
    }

    // Hàm kiểm tra token có hợp lệ không
    public boolean introspect (String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(SIGNER_KEY.getBytes())
                .build()
                .parseClaimsJws(token); // Nếu token bị giả mạo/hết hạn, dòng này sẽ văng lỗi ngay
            return true;
        } catch (Exception e) {
            return false; // Quét thất bại, Token lỏ
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNER_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                // Lấy giá trị đã setSubject lúc tạo token
                .getSubject();
    }
}
