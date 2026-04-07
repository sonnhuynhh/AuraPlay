package com.sonnhuynhh.auraplay.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sonnhuynhh.auraplay.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Tìm user theo username
    Optional<User> findByUsername(String username);
    
    // Kiểm tra user tồn tại theo username
    Boolean existsByUsername(String username);
    
    // Kiểm tra user tồn tại theo email
    Boolean existsByEmail(String email);
}
