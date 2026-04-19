package com.sonnhuynhh.auraplay.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sonnhuynhh.auraplay.dto.request.UserCreationRequest;
import com.sonnhuynhh.auraplay.dto.request.UserUpdateRequest;
import com.sonnhuynhh.auraplay.dto.response.UserResponse;
import com.sonnhuynhh.auraplay.entity.User;
import com.sonnhuynhh.auraplay.exception.AppException;
import com.sonnhuynhh.auraplay.exception.ErrorCode;
import com.sonnhuynhh.auraplay.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Tạo user mới
    @Transactional
    public UserResponse registerUser(UserCreationRequest request) {
        // Nhận vào Request DTO thay vì Entity
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // MAPPING: Từ UserCreationRequest (DTO) -> User (Entity) để lưu vào dtb
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuraBalance(0L);
        user.setRole("USER");

        // Lưu vào dtb
        User savedUser = userRepository.save(user);

        // MAPPING: Từ User (Entity) mới lưu -> UserResponse (DTO) để trả về cho client
        return toUserResponse(savedUser);
    }

    // Lấy tất cả user
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                // Chuyển List thành Stream để xử lý
                .stream()
                // Từng User được chuyển đổi
                .map(this::toUserResponse)
                // Chuyển Stream -> List
                .toList();
    }

    // Lấy user theo id
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        return toUserResponse(user);
    }

    // Lấy user theo username (dùng cho Login/Auth)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        return toUserResponse(user);
    }

    // Cập nhật thông tin user (chỉ update các field cho phép)
    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Chỉ cập nhật các field cho phép, giữ nguyên password, auraBalance, role
        if (request.getUsername() != null) {
            // Kiểm tra username mới không trùng với user khác
            if (!existingUser.getUsername().equals(request.getUsername())
                    && userRepository.existsByUsername(request.getUsername())) {
                throw new AppException(ErrorCode.USERNAME_EXISTED);
            }
            existingUser.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            // Kiểm tra email mới không trùng với user khác
            if (!existingUser.getEmail().equals(request.getEmail())
                    && userRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            existingUser.setEmail(request.getEmail());
        }

        User savedUser = userRepository.save(existingUser);
        return toUserResponse(savedUser);
    }

    // Đổi mật khẩu
    @Transactional
    public void changePassword(UUID id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Xóa user
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    // Hàm tiện ích: chuyển entity User thành UserResponse DTO
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .auraBalance(user.getAuraBalance())
                .role(user.getRole())
                .build();
    }
}
