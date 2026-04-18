package com.sonnhuynhh.auraplay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sonnhuynhh.auraplay.dto.request.ChangePasswordRequest;
import com.sonnhuynhh.auraplay.dto.request.UserCreationRequest;
import com.sonnhuynhh.auraplay.dto.request.UserUpdateRequest;
import com.sonnhuynhh.auraplay.dto.response.ApiResponse;
import com.sonnhuynhh.auraplay.dto.response.UserResponse;
import com.sonnhuynhh.auraplay.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // ========== PUBLIC (Không cần token) ==========

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

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile(Authentication authentication) {
        // authentication.getName(): username đã được xác thực khi quét token
        String username = authentication.getName();
        return ApiResponse.success(userService.getUserByUsername(username));
    }

    // Cập nhật thông tin cá nhân
    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMyProfile(Authentication authentication, @RequestBody UserUpdateRequest request) {
        String username = authentication.getName();

        // Tìm user ID từ username rồi gọi update
        UserResponse currentUser = userService.getUserByUsername(username);

        return ApiResponse.success(userService.updateUser(currentUser.getId(), request));
    }
    
    @PutMapping("/me/password")
    public ApiResponse<String> changePassword(Authentication authentication, @RequestBody @Valid ChangePasswordRequest request) {
        String username = authentication.getName();
        UserResponse currentUser = userService.getUserByUsername(username);
        userService.changePassword(currentUser.getId(), request.getOldPassword(), request.getNewPassword());
        
        return ApiResponse.success("Password changed successfully");
    }

    // ========== ADMIN (Sau này sẽ phân quyền riêng) ==========

    // Xem danh sách tất cả các user
    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUser() {
        return ApiResponse.success(userService.getAllUsers());
    }
    
    // Xem thông tin user theo ID
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable UUID id) {
        return ApiResponse.success(userService.getUserById(id));
    }
    
    // Xóa user theo ID
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ApiResponse.success("User deleted successfully");
    }
}
