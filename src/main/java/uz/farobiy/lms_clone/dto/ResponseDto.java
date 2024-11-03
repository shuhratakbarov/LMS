package uz.farobiy.lms_clone.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;

    public ResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
