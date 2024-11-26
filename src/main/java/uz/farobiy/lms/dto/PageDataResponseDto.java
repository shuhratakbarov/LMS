package uz.farobiy.lms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDataResponseDto<T> {
    private T content;
    private Long totalElements;
}

