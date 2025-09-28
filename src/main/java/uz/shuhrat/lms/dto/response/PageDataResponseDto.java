package uz.shuhrat.lms.dto.response;

public record PageDataResponseDto<T>(
        T content,
        Long totalElements
) {
}