package uz.shuhrat.lms.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GeneralResponseDto<T> {
    private boolean success;
    private String message;
    private T data;

    public GeneralResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static <T> GeneralResponseDto<T> success(String message, T data) {
        return new GeneralResponseDto<>(true, message, data);
    }

    public static <T> GeneralResponseDto<T> error(String message) {
        return new GeneralResponseDto<>(false, message, null);
    }
}
