package com.sonnhuynhh.auraplay.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ trả về những field có giá trị (không null)
public class ApiResponse<T> {

    @Builder.Default
    private int code = 1000; // 1000 là success

    private String message; // Chứa câu thông báo (vd: "Success", "User not found", ...)

    private T data; // Chứa dữ liệu thực tế trả về (có thể là Object, List, String, ...)

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(1000)
                .message("Success")
                .data(data)
                .build(); // Trả về đối tượng ApiResponse đã được build
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build(); // Trả về đối tượng ApiResponse đã được build
    }
}

/*
<T> (Generic): Giúp ApiResponse có thể chứa bất kỳ kiểu dữ liệu nào ở phần data. 
Ví dụ: ApiResponse<User>, ApiResponse<List<Game>>.
@JsonInclude(JsonInclude.Include.NON_NULL): Giả sử API bị lỗi, biến data sẽ null. 
Annotation này giúp ẩn luôn trường data khỏi kết quả JSON trả về cho đẹp mã.
code: Mã lỗi nội bộ của ứng dụng (KHÔNG phải HTTP Status Code). Ví dụ HTTP status luôn
 là 200 (OK), nhưng code bên trong có thể là 1000 (thành công), 1001 (sai mật khẩu), 
 1002 (thiếu Aura)... Phía Frontend sẽ dựa vào code này để xử lý logic.
*/