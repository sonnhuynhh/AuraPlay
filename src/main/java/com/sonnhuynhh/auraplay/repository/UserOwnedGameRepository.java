package com.sonnhuynhh.auraplay.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sonnhuynhh.auraplay.entity.UserOwnedGame;
import com.sonnhuynhh.auraplay.entity.UserOwnedGameId;

@Repository
public interface UserOwnedGameRepository extends JpaRepository<UserOwnedGame, UserOwnedGameId> {
    // Tìm tất cả game của một user
    List<UserOwnedGame> findByUserId(UUID userId);

    // Kiểm tra user có sở hữu game không
    Boolean existsById_UserIdAndId_GameId(UUID userId, Integer gameId);

    // Tìm game theo user và game
    Optional<UserOwnedGame> findById_UserIdAndId_GameId(UUID userId, Integer gameId);
}
