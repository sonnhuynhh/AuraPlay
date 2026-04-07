package com.sonnhuynhh.auraplay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sonnhuynhh.auraplay.entity.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    // Tìm game theo tên
    Optional<Game> findByTitle(String title);

    // Kiểm tra game tồn tại theo title
    Boolean existsByTitle(String title);

    // Tìm tất cả game đã được xuất bản
    List<Game> findByIsPublishedTrue();

    // Tìm tất cả game chưa được xuất bản
    List<Game> findByIsPublishedFalse();

    // Tìm game theo title và đã được xuất bản
    Optional<Game> findByTitleAndIsPublishedTrue(String title);
}
