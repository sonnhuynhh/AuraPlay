package com.sonnhuynhh.auraplay.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sonnhuynhh.auraplay.dto.response.PaymentTransactionResponse;
import com.sonnhuynhh.auraplay.entity.PaymentPackage;
import com.sonnhuynhh.auraplay.entity.PaymentTransaction;
import com.sonnhuynhh.auraplay.entity.User;
import com.sonnhuynhh.auraplay.exception.AppException;
import com.sonnhuynhh.auraplay.exception.ErrorCode;
import com.sonnhuynhh.auraplay.repository.PaymentPackageRepository;
import com.sonnhuynhh.auraplay.repository.PaymentTransactionRepository;
import com.sonnhuynhh.auraplay.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserRepository userRepository;
    private final PaymentPackageRepository packageRepository;

    // Tạo yêu cầu thanh toán (giao dịch PENDING)
    @Transactional
    public PaymentTransactionResponse createPaymentRequest(UUID userId, Integer packageId, String currency, String paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PaymentPackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new AppException(ErrorCode.PACKAGE_NOT_FOUND));

        // Kiểm tra gói nạp còn hoạt động không
        if (!pkg.getIsActive()) {
            throw new AppException(ErrorCode.PACKAGE_INACTIVE);
        }

        // Lấy số tiền thật (fiat) dựa trên loại tiền tệ
        BigDecimal fiatAmount = "USD".equals(currency) ? pkg.getPriceUsd() : pkg.getPriceVnd();

        // Tự sinh mã giao dịch
        String transCode = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Tạo giao dịch PENDING
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setUser(user);
        transaction.setFiatAmount(fiatAmount); // Số tiền thật (VND/USD)
        transaction.setAuraReward(pkg.getAuraReward()); // Lượng Aura sẽ nhận
        transaction.setPaymentMethod(paymentMethod); // Phương thức thanh toán
        transaction.setStatus("PENDING"); // Trạng thái giao dịch
        transaction.setTransactionCode(transCode); // Mã giao dịch
        
        return toResponse(paymentTransactionRepository.save(transaction));
    }

    // Hoàn tất thanh toán — cộng Aura cho user
    @Transactional
    public PaymentTransactionResponse completePayment(String transCode) {
        PaymentTransaction transaction = paymentTransactionRepository.findByTransactionCode(transCode)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!"PENDING".equals(transaction.getStatus())) {
            throw new AppException(ErrorCode.TRANSACTION_PROCESSED);
        }

        transaction.setStatus("SUCCESS");
        paymentTransactionRepository.save(transaction);

        // Cộng Aura vào tài khoản user
        User user = transaction.getUser();
        user.setAuraBalance(user.getAuraBalance() + transaction.getAuraReward());
        userRepository.save(user);

        return toResponse(transaction);
    }

    // Hủy giao dịch khi thanh toán thất bại
    @Transactional
    public PaymentTransactionResponse failPayment(String transCode) {
        PaymentTransaction transaction = paymentTransactionRepository.findByTransactionCode(transCode)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!"PENDING".equals(transaction.getStatus())) {
            throw new AppException(ErrorCode.TRANSACTION_PROCESSED);
        }

        transaction.setStatus("FAILED");
        paymentTransactionRepository.save(transaction);

        return toResponse(transaction);
    }

    // Lấy lịch sử giao dịch của user
    public List<PaymentTransactionResponse> getPaymentHistory(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return paymentTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Lấy tất cả giao dịch theo trạng thái (dành cho Admin)
    public List<PaymentTransactionResponse> getTransactionsByStatus(String status) {
        return paymentTransactionRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Hàm tiện ích: Entity -> DTO
    private PaymentTransactionResponse toResponse(PaymentTransaction transaction) {
        return PaymentTransactionResponse.builder()
                .id(transaction.getId())
                .fiatAmount(transaction.getFiatAmount())
                .auraReward(transaction.getAuraReward())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .transactionCode(transaction.getTransactionCode())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
