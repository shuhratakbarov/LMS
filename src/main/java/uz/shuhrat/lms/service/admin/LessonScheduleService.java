package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.LessonScheduleDTO;

public interface LessonScheduleService {
    ResponseDto<?> getAdminSchedule(String keyword, int page, int size);

    ResponseDto<?> checkForScheduleConflicts(LessonScheduleDTO lessonScheduleDTO, Long lessonScheduleId) throws Exception;

    ResponseDto<?> createLessonSchedule(LessonScheduleDTO lessonScheduleDTO) throws Exception;

    ResponseDto<?> updateLessonSchedule(Long scheduleId, LessonScheduleDTO scheduleDTO) throws Exception;

    ResponseDto<?> deleteLessonSchedule(Long scheduleId);
}
