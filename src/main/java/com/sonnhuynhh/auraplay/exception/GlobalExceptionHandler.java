package com.sonnhuynhh.auraplay.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sonnhuynhh.auraplay.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice // Giúp bắt lỗi ở tất cả các Controller
@Slf4j // Giúp in log lỗi ra console
public class GlobalExceptionHandler {

    // 1. Bắt lỗi ta chủ động ném ra (AppException)
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        // Trả về ApiResponse chứa mã lỗi và câu thông báo
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());

        // Gắn HTTP Status Code tương ứng vào (vd: 400 Bad Request, 404 Not Found)
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // 2. Bắt tất cả các lỗi khác (ngoài AppException)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Void>> handlingRuntimeException(Exception exception) {
        // Log lại lỗi chi tiết
        log.error("Exception: ", exception);

        // Lấy mã lỗi "Không phân loại"
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        // Trả về một format lỗi chuẩn
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // 3. Bắt các lỗi do người dùng nhập liệu sai (Validation Exception)
    @ExceptionHandler(value = org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handlingValidation(org.springframework.web.bind.MethodArgumentNotValidException exception) {
        
        // Trích xuất lấy đúng cái câu chữ mà chúng ta đã cấu hình trong DTO
        // VD: "Username cannot be blank" hay "Password must be as least 6 characters"
        String errorMessage = exception.getFieldError().getDefaultMessage();

        // 400 là mã lỗi thường quy ước cho những lỗi nhập sai từ Frontend
        ApiResponse<Void> apiResponse = ApiResponse.error(400, errorMessage);

        // badRequest tương đương HTTP Status Code 400
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
