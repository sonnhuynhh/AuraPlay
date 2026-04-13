package com.sonnhuynhh.auraplay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sonnhuynhh.auraplay.entity.PaymentPackage;

@Repository
public interface PaymentPackageRepository extends JpaRepository<PaymentPackage, Integer> {

    // Tìm tất cả gói nạp đang hoạt động theo số lượng Aura tăng dần
    List<PaymentPackage> findByIsActiveTrueOrderByAuraRewardAsc();

    // Tìm gói nạp theo số lượng Aura
    Optional<PaymentPackage> findByAuraReward(Long auraReward);

    // Tìm gói nạp theo tên tiếng Việt
    Optional<PaymentPackage> findByNameVi(String nameVi);

    // Tìm gói nạp theo tên tiếng Anh
    Optional<PaymentPackage> findByNameEn(String nameEn);

    // Kiểm tra gói nạp tồn tại theo số lượng Aura
    boolean existsByAuraReward(Long auraReward);

    // Kiểm tra gói nạp tồn tại theo tên tiếng Việt
    boolean existsByNameVi(String nameVi);

    // Kiểm tra gói nạp tồn tại theo tên tiếng Anh
    boolean existsByNameEn(String nameEn);
}
