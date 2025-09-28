package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.LessonScheduleRequestDto;
import uz.shuhrat.lms.dto.response.LessonScheduleResponseDto;

public interface LessonScheduleService {
    GeneralResponseDto<?> getAdminSchedule(String keyword, int page, int size);

    GeneralResponseDto<?> getTeacherSchedule();

    GeneralResponseDto<?> getStudentSchedule();

    GeneralResponseDto<?> checkForScheduleConflicts(LessonScheduleRequestDto lessonScheduleResponseDTO, Long lessonScheduleId) throws Exception;

    GeneralResponseDto<?> createLessonSchedule(LessonScheduleRequestDto lessonScheduleResponseDTO) throws Exception;

    GeneralResponseDto<?> updateLessonSchedule(Long scheduleId, LessonScheduleRequestDto scheduleDTO) throws Exception;

    GeneralResponseDto<?> deleteLessonSchedule(Long scheduleId);
}
