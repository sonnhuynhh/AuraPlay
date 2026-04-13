package com.sonnhuynhh.auraplay.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    
    // Hệ thống
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    // User (Bắt đầu bằng 10XX)
    USERNAME_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "Email already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
    INCORRECT_PASSWORD(1004, "Incorrect password", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),

    // Game & Aura (Bắt đầu bằng 20XX)
    GAME_NOT_FOUND(2001, "Game not found", HttpStatus.NOT_FOUND),
    GAME_TITLE_EXISTED(2002, "Game title already exists", HttpStatus.BAD_REQUEST),
    GAME_NOT_PUBLISHED(2003, "Game is not available for purchase", HttpStatus.BAD_REQUEST),
    ALREADY_OWNED_GAME(2004, "User already owns this game", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_AURA(2005, "User does not have enough Aura", HttpStatus.BAD_REQUEST),

    // Payment (Bắt đầu bằng 30XX)
    PACKAGE_NOT_FOUND(3001, "Package not found", HttpStatus.NOT_FOUND),
    PACKAGE_INACTIVE(3002, "Payment package is no longer active", HttpStatus.BAD_REQUEST),
    TRANSACTION_EXISTED(3003, "Transaction code already exists", HttpStatus.BAD_REQUEST),
    TRANSACTION_NOT_FOUND(3004, "Transaction not fount", HttpStatus.NOT_FOUND),
    TRANSACTION_PROCESSED(3005, "Transaction already processed", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
