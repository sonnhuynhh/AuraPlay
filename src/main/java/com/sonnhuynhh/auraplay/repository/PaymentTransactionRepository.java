package com.sonnhuynhh.auraplay.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sonnhuynhh.auraplay.entity.PaymentTransaction;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    // Tìm giao dịch theo mã giao dịch
    Optional<PaymentTransaction> findByTransactionCode(String transactionCode);

    // Kiểm tra giao dịch tồn tại theo mã giao dịch
    Boolean existsByTransactionCode(String transactionCode);

    // Lấy tất cả giao dịch của một user theo thời gian giảm dần
    List<PaymentTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Lấy tất cả giao dịch theo trạng thái và thời gian giảm dần
    List<PaymentTransaction> findByStatusOrderByCreatedAtDesc(String status);
}
