package com.sonnhuynhh.auraplay.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sonnhuynhh.auraplay.dto.request.AuthenticationRequest;
import com.sonnhuynhh.auraplay.dto.response.ApiResponse;
import com.sonnhuynhh.auraplay.dto.response.AuthenticationResponse;
import com.sonnhuynhh.auraplay.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        // Chuyền thông tin (username, password) xuống cho service xử lý
        AuthenticationResponse result = authenticationService.authenticate(request);
        
        // Bọc kết quả vào vỏ ApiResponse rồi trả về cho người dùng
        return ApiResponse.success(result);
    }
    
}
