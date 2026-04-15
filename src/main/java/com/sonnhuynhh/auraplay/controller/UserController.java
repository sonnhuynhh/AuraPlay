package com.sonnhuynhh.auraplay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sonnhuynhh.auraplay.dto.request.UserCreationRequest;
import com.sonnhuynhh.auraplay.dto.response.ApiResponse;
import com.sonnhuynhh.auraplay.dto.response.UserResponse;
import com.sonnhuynhh.auraplay.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


// Đánh dấu đây là nơi tiếp nhận Request từ Internet
@RestController
// Đường dẫn gốc của controller này là /users
@RequestMapping("/users")
// Tự động Inject các dependency
@RequiredArgsConstructor
public class UserController {
    
    // Tiêm UserService vào để gọi hàm
    private final UserService userService;

    // định nghĩa loại request là POST (tạo mới dữ liệu)
    @PostMapping
    // @RequestBody: Ép Spring đọc JSON từ body request và chuyển thành Object UserCreationRequest
    // @Valid: Ép Spring chạy Validation (kiểm tra @NotBlank, @Size) trên Object vừa nhận
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        
        // Gọi service để xử lý logic
        UserResponse result = userService.registerUser(request);

        // Đóng gói kết quả (UserResponse) vào khung chuẩn ApiResponse rồi trả về
        return ApiResponse.success(result);
    }

    @GetMapping("/test")
    public ApiResponse<String> testToken() {
        return ApiResponse.success("Wow! Token xịn! Bạn đã vượt qua chốt gác thành công!");
    }
    
}
