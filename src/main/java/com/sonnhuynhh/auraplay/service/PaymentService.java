package com.sonnhuynhh.auraplay.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
    public PaymentTransaction createPaymentRequest(UUID userId, Integer packageId, String currency, String transCode, String paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PaymentPackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new AppException(ErrorCode.PACKAGE_NOT_FOUND));

        // Kiểm tra gói nạp còn hoạt động không
        if (!pkg.getIsActive()) {
            throw new AppException(ErrorCode.PACKAGE_INACTIVE);
        }

        // Kiểm tra giao dịch đã tồn tại chưa
        if (paymentTransactionRepository.existsByTransactionCode(transCode)) {
            throw new AppException(ErrorCode.TRANSACTION_EXISTED);
        }

        // Lấy số tiền thật (fiat) dựa trên loại tiền tệ
        BigDecimal fiatAmount = "USD".equals(currency) ? pkg.getPriceUsd() : pkg.getPriceVnd();

        // Tạo giao dịch PENDING
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setUser(user);
        paymentTransaction.setFiatAmount(fiatAmount); // Số tiền thật (VND/USD)
        paymentTransaction.setAuraReward(pkg.getAuraReward()); // Lượng Aura sẽ nhận
        paymentTransaction.setPaymentMethod(paymentMethod); // Phương thức thanh toán
        paymentTransaction.setStatus("PENDING"); // Trạng thái giao dịch
        paymentTransaction.setTransactionCode(transCode); // Mã giao dịch
        return paymentTransactionRepository.save(paymentTransaction);
    }

    // Hoàn tất thanh toán — cộng Aura cho user
    @Transactional
    public void completePayment(String transCode) {
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
    }

    // Hủy giao dịch khi thanh toán thất bại
    @Transactional
    public void failPayment(String transCode) {
        PaymentTransaction transaction = paymentTransactionRepository.findByTransactionCode(transCode)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!"PENDING".equals(transaction.getStatus())) {
            throw new AppException(ErrorCode.TRANSACTION_PROCESSED);
        }

        transaction.setStatus("FAILED");
        paymentTransactionRepository.save(transaction);
    }

    // Lấy lịch sử giao dịch của user
    public List<PaymentTransaction> getPaymentHistory(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return paymentTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Lấy tất cả giao dịch theo trạng thái (dành cho Admin)
    public List<PaymentTransaction> getTransactionsByStatus(String status) {
        return paymentTransactionRepository.findByStatusOrderByCreatedAtDesc(status);
    }
}
