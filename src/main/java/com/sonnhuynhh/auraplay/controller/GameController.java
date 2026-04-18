package com.sonnhuynhh.auraplay.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sonnhuynhh.auraplay.dto.request.GameCreateRequest;
import com.sonnhuynhh.auraplay.dto.request.GameUpdateRequest;
import com.sonnhuynhh.auraplay.dto.response.ApiResponse;
import com.sonnhuynhh.auraplay.dto.response.GameResponse;
import com.sonnhuynhh.auraplay.dto.response.UserResponse;
import com.sonnhuynhh.auraplay.service.GameService;
import com.sonnhuynhh.auraplay.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    // ========== PUBLIC (Không cần token) ==========

    // Xem danh sách game đã được xuất bản
    @GetMapping()
    public ApiResponse<List<GameResponse>> getPublishedGames() {
        return ApiResponse.success(gameService.getAllPublishedGames());
    }

    // Xem chi tiết 1 game
    @GetMapping("/{id}")
    public ApiResponse<GameResponse> getGameById(@PathVariable Integer id) {
        return ApiResponse.success(gameService.getGameById(id));
    }

    // ========== Cần token ==========
    
    // Mua game bằng Aura
    @PostMapping("/{gameId}/purchase")
    public ApiResponse<String> purchaseGame(Authentication authentication, @PathVariable Integer gameId) {
        // Lấy username từ Token -> tìm userId
        String username = authentication.getName();
        UserResponse currentUser = userService.getUserByUsername(username);

        gameService.purchaseGame(currentUser.getId(), gameId);
        return ApiResponse.success("Game purchased successfully!");
    }

    // Xem danh sách game đã sở hữu
    @GetMapping("/my-games")
    public ApiResponse<List<GameResponse>> getMyGames(Authentication authentication) {
        String username = authentication.getName();
        UserResponse currentUser = userService.getUserByUsername(username);
        return ApiResponse.success(gameService.getOwnedGames(currentUser.getId()));
    }

    // ========== ADMIN ==========

    // Tạo game mới
    @PostMapping
    public ApiResponse<GameResponse> createGame(@RequestBody @Valid GameCreateRequest request) {
        return ApiResponse.success(gameService.createGame(request));
    }

    // Xem tất cả game (bao gồm cả chưa publish)
    @GetMapping("/admin/all")
    public ApiResponse<List<GameResponse>> getAllGames() {
        return ApiResponse.success(gameService.getAllGames());
    }

    // Cập nhật game
    @PutMapping("/{id}")
    public ApiResponse<GameResponse> updateGame(@PathVariable Integer id, @RequestBody GameUpdateRequest request) {
        return ApiResponse.success(gameService.updateGame(id, request));
    }
    
    // Xóa game
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteGame(@PathVariable Integer id) {
        gameService.deleteGame(id);
        return ApiResponse.success("Game deleted successfully!");
    }
}
