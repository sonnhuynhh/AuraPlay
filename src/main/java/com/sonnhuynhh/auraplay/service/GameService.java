package com.sonnhuynhh.auraplay.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sonnhuynhh.auraplay.dto.request.GameCreateRequest;
import com.sonnhuynhh.auraplay.dto.request.GameUpdateRequest;
import com.sonnhuynhh.auraplay.dto.response.GameResponse;
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
    public List<GameResponse> getOwnedGames(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return userOwnedGameRepository.findByUserId(userId)
                .stream()
                .map(owned -> toGameResponse(owned.getGame()))
                .toList();
    }

    // Tạo game mới
    @Transactional
    public GameResponse createGame(GameCreateRequest request) {
        if (gameRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCode.GAME_TITLE_EXISTED);
        }
        Game game = new Game();
        game.setTitle(request.getTitle());
        game.setDescriptionEn(request.getDescriptionEn());
        game.setDescriptionVi(request.getDescriptionVi());
        game.setAuraPrice(request.getAuraPrice());
        game.setThumbnailUrl(request.getThumbnailUrl());
        game.setGameUrl(request.getGameUrl());
        game.setIsPublished(false);
        return toGameResponse(gameRepository.save(game));
    }

    // Lấy tất cả game (đã xuất bản)
    public List<GameResponse> getAllPublishedGames() {
        return gameRepository.findByIsPublishedTrue()
                .stream()
                .map(this::toGameResponse)
                .toList();
    }

    // Lấy tất cả game (bao gồm chưa xuất bản - dành cho Admin)
    public List<GameResponse> getAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(this::toGameResponse)
                .toList();
    }

    // Lấy game theo id
    public GameResponse getGameById(Integer id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GAME_NOT_FOUND));
        return toGameResponse(game);
    }

    // Cập nhật game (chỉ update các field cụ thể)
    @Transactional
    public GameResponse updateGame(Integer id, GameUpdateRequest request) {
        Game existingGame = gameRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GAME_NOT_FOUND));

        // Chỉ cập nhật các field được truyền vào
        if (request.getTitle() != null) {
            // Kiểm tra title mới không trùng với game khác
            if (!existingGame.getTitle().equals(request.getTitle())
                    && gameRepository.existsByTitle(request.getTitle())) {
                throw new AppException(ErrorCode.GAME_TITLE_EXISTED);
            }
            existingGame.setTitle(request.getTitle());
        }
        if (request.getDescriptionEn() != null) {
            existingGame.setDescriptionEn(request.getDescriptionEn());
        }
        if (request.getDescriptionVi() != null) {
            existingGame.setDescriptionVi(request.getDescriptionVi());
        }
        if (request.getAuraPrice() != null) {
            existingGame.setAuraPrice(request.getAuraPrice());
        }
        if (request.getThumbnailUrl() != null) {
            existingGame.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getGameUrl() != null) {
            existingGame.setGameUrl(request.getGameUrl());
        }
        if (request.getIsPublished() != null) {
            existingGame.setIsPublished(request.getIsPublished());
        }

        return toGameResponse(gameRepository.save(existingGame));
    }

    // Xóa game
    @Transactional
    public void deleteGame(Integer id) {
        if (!gameRepository.existsById(id)) {
            throw new AppException(ErrorCode.GAME_NOT_FOUND);
        }
        gameRepository.deleteById(id);
    }

    // Hàm tiện ích: Entity -> DTO
    private GameResponse toGameResponse(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .descriptionEn(game.getDescriptionEn())
                .descriptionVi(game.getDescriptionVi())
                .auraPrice(game.getAuraPrice())
                .thumbnailUrl(game.getThumbnailUrl())
                .gameUrl(game.getGameUrl())
                .isPublished(game.getIsPublished())
                .createdAt(game.getCreatedAt())
                .updatedAt(game.getUpdatedAt())
                .build();
    }
}
