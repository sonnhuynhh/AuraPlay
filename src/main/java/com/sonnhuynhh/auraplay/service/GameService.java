package com.sonnhuynhh.auraplay.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sonnhuynhh.auraplay.entity.Game;
import com.sonnhuynhh.auraplay.entity.User;
import com.sonnhuynhh.auraplay.entity.UserOwnedGame;
import com.sonnhuynhh.auraplay.entity.UserOwnedGameId;
import com.sonnhuynhh.auraplay.exception.AppException;
import com.sonnhuynhh.auraplay.exception.ErrorCode;
import com.sonnhuynhh.auraplay.repository.GameRepository;
import com.sonnhuynhh.auraplay.repository.UserOwnedGameRepository;
import com.sonnhuynhh.auraplay.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final UserOwnedGameRepository userOwnedGameRepository;

    // Mua game bằng Aura
    @Transactional
    public void purchaseGame(UUID userId, Integer gameId) {
        // Kiểm tra user và game có tồn tại không
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new AppException(ErrorCode.GAME_NOT_FOUND));

        // Kiểm tra game đã được xuất bản chưa
        if (!game.getIsPublished()) {
            throw new AppException(ErrorCode.GAME_NOT_PUBLISHED);
        }

        // Kiểm tra user đã sở hữu game chưa
        if (userOwnedGameRepository.existsById_UserIdAndId_GameId(userId, gameId)) {
            throw new AppException(ErrorCode.ALREADY_OWNED_GAME);
        }

        // Kiểm tra game miễn phí
        if (game.getAuraPrice() > 0) {
            // Kiểm tra user có đủ Aura để mua game không
            if (user.getAuraBalance() < game.getAuraPrice()) {
                throw new AppException(ErrorCode.NOT_ENOUGH_AURA);
            }

            // Trừ Aura của user
            user.setAuraBalance(user.getAuraBalance() - game.getAuraPrice());
            userRepository.save(user);
        }

        // Thêm game vào danh sách game đã sở hữu của user
        UserOwnedGame userOwnedGame = new UserOwnedGame();
        userOwnedGame.setId(new UserOwnedGameId(userId, gameId));
        userOwnedGame.setUser(user);
        userOwnedGame.setGame(game);
        userOwnedGameRepository.save(userOwnedGame);
    }

    // Lấy danh sách game user đã sở hữu
    public List<UserOwnedGame> getOwnedGames(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return userOwnedGameRepository.findByUserId(userId);
    }

    // Tạo game mới
    @Transactional
    public Game createGame(Game game) {
        if (gameRepository.existsByTitle(game.getTitle())) {
            throw new AppException(ErrorCode.GAME_TITLE_EXISTED);
        }
        return gameRepository.save(game);
    }

    // Lấy tất cả game (đã xuất bản)
    public List<Game> getAllPublishedGames() {
        return gameRepository.findByIsPublishedTrue();
    }

    // Lấy tất cả game (bao gồm chưa xuất bản - dành cho Admin)
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    // Lấy game theo id
    public Optional<Game> getGameById(Integer id) {
        return gameRepository.findById(id);
    }

    // Cập nhật game (chỉ update các field cụ thể)
    @Transactional
    public Game updateGame(Integer id, Game updatedData) {
        Game existingGame = gameRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GAME_NOT_FOUND));

        // Chỉ cập nhật các field được truyền vào
        if (updatedData.getTitle() != null) {
            // Kiểm tra title mới không trùng với game khác
            if (!existingGame.getTitle().equals(updatedData.getTitle())
                    && gameRepository.existsByTitle(updatedData.getTitle())) {
                throw new AppException(ErrorCode.GAME_TITLE_EXISTED);
            }
            existingGame.setTitle(updatedData.getTitle());
        }
        if (updatedData.getDescriptionEn() != null) {
            existingGame.setDescriptionEn(updatedData.getDescriptionEn());
        }
        if (updatedData.getDescriptionVi() != null) {
            existingGame.setDescriptionVi(updatedData.getDescriptionVi());
        }
        if (updatedData.getAuraPrice() != null) {
            existingGame.setAuraPrice(updatedData.getAuraPrice());
        }
        if (updatedData.getThumbnailUrl() != null) {
            existingGame.setThumbnailUrl(updatedData.getThumbnailUrl());
        }
        if (updatedData.getGameUrl() != null) {
            existingGame.setGameUrl(updatedData.getGameUrl());
        }
        if (updatedData.getIsPublished() != null) {
            existingGame.setIsPublished(updatedData.getIsPublished());
        }

        return gameRepository.save(existingGame);
    }

    // Xóa game
    @Transactional
    public void deleteGame(Integer id) {
        if (!gameRepository.existsById(id)) {
            throw new AppException(ErrorCode.GAME_NOT_FOUND);
        }
        gameRepository.deleteById(id);
    }
}
