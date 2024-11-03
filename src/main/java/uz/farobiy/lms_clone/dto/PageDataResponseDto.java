package uz.farobiy.lms_clone.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class  PageDataResponseDto<T> {
    private T content;
    private Long totalElements;
// private int page;
// private int size;
}

