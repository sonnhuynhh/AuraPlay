package com.sonnhuynhh.auraplay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private boolean authenticated; // báo hiệu đã đăng nhập thành công
    private String token; // chứa dãy JWT (chỉ khi authenticated = true)
}
