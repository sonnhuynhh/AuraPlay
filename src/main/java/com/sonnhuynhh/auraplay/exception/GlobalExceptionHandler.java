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
}
