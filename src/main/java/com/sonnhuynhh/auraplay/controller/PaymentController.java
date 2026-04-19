package com.sonnhuynhh.auraplay.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sonnhuynhh.auraplay.dto.request.PaymentRequest;
import com.sonnhuynhh.auraplay.dto.response.ApiResponse;
import com.sonnhuynhh.auraplay.dto.response.PaymentTransactionResponse;
import com.sonnhuynhh.auraplay.dto.response.UserResponse;
import com.sonnhuynhh.auraplay.service.PaymentService;
import com.sonnhuynhh.auraplay.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    private final UserService userService;

    // ========== Cần token (User logged in) ==========
    
    // Tạo yêu cầu nạp tiền (giao dịch PENDING)
    @PostMapping
    public ApiResponse<PaymentTransactionResponse> createPayment(Authentication authentication, @RequestBody @Valid PaymentRequest request) {
        String username = authentication.getName();
        UserResponse currentUser = userService.getUserByUsername(username);
        
        return ApiResponse.success(paymentService.createPaymentRequest(
                            currentUser.getId(),
                            request.getPackageId(),
                            request.getCurrency(),
                            request.getPaymentMethod()
                        ));
    }
    
    // Xem lịch sử giao dịch của bản thân
    @GetMapping("/history")
    public ApiResponse<List<PaymentTransactionResponse>> getMyPaymentHistory(Authentication authentication) {
        String username = authentication.getName();
        UserResponse currentUser = userService.getUserByUsername(username);

        return ApiResponse.success(paymentService.getPaymentHistory(currentUser.getId()));
    }

    // ========== ADMIN ==========

    // Xác nhận thanh toán thành công (Mô phỏng webhook từ cổng thanh toán)
    @PostMapping("/{transCode}/complete")
    public ApiResponse<PaymentTransactionResponse> completePayment(@PathVariable String transCode) {
        return ApiResponse.success(paymentService.completePayment(transCode));
    }

    // Đánh dấu thanh toán thất bại
    @PostMapping("/{transCode}/fail")
    public ApiResponse<PaymentTransactionResponse> failPayment(@PathVariable String transCode) {
        return ApiResponse.success(paymentService.failPayment(transCode));
    }

    // Xem giao dịch theo trạng thái (PENDING, SUCCESS, FAILED)
    @GetMapping("/admin")
    public ApiResponse<List<PaymentTransactionResponse>> getTransactionByStatus(@RequestParam String status) {
        return ApiResponse.success(paymentService.getTransactionsByStatus(status));
    }
}
