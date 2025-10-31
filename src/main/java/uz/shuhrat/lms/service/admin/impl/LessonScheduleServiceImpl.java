package uz.shuhrat.lms.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.domain.LessonSchedule;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.LessonAttendanceRepository;
import uz.shuhrat.lms.db.repository.LessonInstanceRepository;
import uz.shuhrat.lms.db.repository.LessonScheduleRepository;
import uz.shuhrat.lms.db.repository.RoomRepository;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.response.PageDataResponseDto;
import uz.shuhrat.lms.dto.request.LessonScheduleRequestDto;
import uz.shuhrat.lms.dto.response.LessonScheduleResponseDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.admin.LessonScheduleService;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonScheduleServiceImpl implements LessonScheduleService {
    private final LessonScheduleRepository lessonScheduleRepository;
    private final LessonInstanceRepository lessonInstanceRepository;
    private final GroupRepository groupRepository;
    private final RoomRepository roomRepository;
    private final LessonAttendanceRepository lessonAttendanceRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LessonScheduleServiceImpl(LessonScheduleRepository lessonScheduleRepository, LessonInstanceRepository lessonInstanceRepository, GroupRepository groupRepository, RoomRepository roomRepository, LessonAttendanceRepository lessonAttendanceRepository, JdbcTemplate jdbcTemplate) {
        this.lessonScheduleRepository = lessonScheduleRepository;
        this.lessonInstanceRepository = lessonInstanceRepository;
        this.groupRepository = groupRepository;
        this.roomRepository = roomRepository;
        this.lessonAttendanceRepository = lessonAttendanceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public GeneralResponseDto<?> getAdminSchedule(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<LessonSchedule> pages = lessonScheduleRepository.getLessonSchedule(keyword, pageable);
        List<LessonScheduleResponseDto> list = pages.getContent().stream().map(ls ->
                        new LessonScheduleResponseDto(ls.getId(), ls.getGroup().getId(), ls.getGroup().getName(), ls.getDay(), ls.getStartTime(), ls.getEndTime(), ls.getRoom().getId(), ls.getRoom().getName()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<LessonScheduleResponseDto>> dto = new PageDataResponseDto<>(list, pages.getTotalElements());
        return new GeneralResponseDto<>(true, "ok", dto);
    }

    @Override
    public GeneralResponseDto<?> getTeacherSchedule() {
        User user = SecurityHelper.getCurrentUser();
        if (user != null && user.getId() != null) {
            return new GeneralResponseDto<>(true, "ok", lessonScheduleRepository.getTeacherLessonSchedule(user.getId()));
        }
        return new GeneralResponseDto<>(false, "User not found");
    }

    @Override
    public GeneralResponseDto<?> getStudentSchedule() {
        User user = SecurityHelper.getCurrentUser();
        if (user != null && user.getId() != null) {
            return new GeneralResponseDto<>(true, "ok", lessonScheduleRepository.getStudentLessonSchedule(user.getId()));
        }
        return new GeneralResponseDto<>(false, "User not found");
    }

    @Transactional
    @Override
    public GeneralResponseDto<?> createLessonSchedule(LessonScheduleRequestDto lessonScheduleDTO) throws Exception {

        if (!checkForScheduleConflicts(lessonScheduleDTO, 0L).isSuccess()) {
            return new GeneralResponseDto<>(false, "Occupied");
        }

        LessonSchedule schedule = new LessonSchedule();
        schedule.setGroup(groupRepository.findById(lessonScheduleDTO.groupId())
                .orElseThrow(() -> new RuntimeException("Group not found")));
        schedule.setDay(lessonScheduleDTO.day());
        schedule.setStartTime(lessonScheduleDTO.startTime());
        schedule.setEndTime(lessonScheduleDTO.endTime());
        schedule.setRoom(roomRepository.findById(lessonScheduleDTO.roomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));

        schedule = lessonScheduleRepository.save(schedule);

//        generateLessonInstances(schedule, LocalDate.now());

        return new GeneralResponseDto<>(true, "ok", schedule);
    }

    @Transactional
    @Override
    public GeneralResponseDto<?> updateLessonSchedule(Long scheduleId, LessonScheduleRequestDto lessonScheduleDTO) throws Exception {
        LessonSchedule schedule = lessonScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Lesson schedule not found"));

        if (!checkForScheduleConflicts(lessonScheduleDTO, scheduleId).isSuccess()) {
            return new GeneralResponseDto<>(false, "Occupied");
        }

        Group group = groupRepository.findById(lessonScheduleDTO.groupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        schedule.setGroup(group);
        schedule.setDay(lessonScheduleDTO.day());
        schedule.setStartTime(lessonScheduleDTO.startTime());
        schedule.setEndTime(lessonScheduleDTO.endTime());
        schedule.setRoom(roomRepository.findById(lessonScheduleDTO.roomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));

//        LocalDate lastLessonDate = lessonAttendanceRepository.findTopLessonInstanceByLessonScheduleOrderByLessonDateDesc(schedule)
//                .map(LessonInstance::getLessonDate)
//                .orElse(LocalDate.MIN);
//
//        lessonInstanceRepository.deleteByLessonScheduleIdAndLessonDateAfter(schedule.getId(), lastLessonDate);
//        generateLessonInstances(schedule, lastLessonDate);

        return new GeneralResponseDto<>(true, "ok", schedule);
    }

    @Override
    public GeneralResponseDto<?> deleteLessonSchedule(Long scheduleId) {
        LessonSchedule schedule = lessonScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Lesson schedule not found"));

        lessonScheduleRepository.delete(schedule);
        return new GeneralResponseDto<>(true, "Lesson schedule deleted successfully");
    }

    @Override
    public GeneralResponseDto<?> checkForScheduleConflicts(LessonScheduleRequestDto scheduleDTO, Long scheduleId) throws Exception {
        Integer day = scheduleDTO.day();
        Integer startTime = scheduleDTO.startTime();
        Integer endTime = scheduleDTO.endTime();
        Long roomId = scheduleDTO.roomId();
        Long groupId = scheduleDTO.groupId();

        // Check for room conflicts
        List<LessonSchedule> roomSchedules = lessonScheduleRepository.findByRoomId(roomId);
        for (LessonSchedule existing : roomSchedules) {
            if (existing.getId().equals(scheduleId)) continue;
            if (!existing.getDay().equals(day)) continue;

            if (existing.getStartTime() < endTime && startTime < existing.getEndTime()) {
                return new GeneralResponseDto<>(false, "Room " + existing.getRoom().getName() + " is booked from " +
                                                       existing.getStartTime() + ":00 to " + existing.getEndTime() + ":00 on " +
                                                       DayOfWeek.of(day) + " by group " + existing.getGroup().getName());
            }
        }

        // Check for group conflicts
        List<LessonSchedule> groupSchedules = lessonScheduleRepository.findByGroupId(groupId);
        for (LessonSchedule existing : groupSchedules) {
            if (existing.getId().equals(scheduleId)) continue;
            if (!existing.getDay().equals(day)) continue;

            if (existing.getStartTime() < endTime && startTime < existing.getEndTime()) {
                return new GeneralResponseDto<>(false, "Group " + existing.getGroup().getName() + " already has a lesson from " +
                                                       existing.getStartTime() + ":00 to " + existing.getEndTime() + ":00 on " +
                                                       DayOfWeek.of(day) + " in room " + existing.getRoom().getName());
            }
        }

        // Check for teacher conflicts
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new Exception("Group not found with ID: " + groupId));
        User teacher = group.getTeacher();

        List<LessonSchedule> teacherSchedules = lessonScheduleRepository.findByGroupTeacherId(teacher.getId());

        for (LessonSchedule existing : teacherSchedules) {
            if (existing.getId().equals(scheduleId)) continue;
            if (!existing.getDay().equals(day)) continue;

            if (existing.getStartTime() < endTime && startTime < existing.getEndTime()) {

                return new GeneralResponseDto<>(false, "Teacher " + teacher.getUsername() + " already has a lesson from " +
                                                       existing.getStartTime() + ":00 to " + existing.getEndTime() + ":00 on " +
                                                       DayOfWeek.of(day) + " with group " + existing.getGroup().getName());
            }
        }

        return new GeneralResponseDto<>(true, "ok");
    }

//    private void generateLessonInstances(LessonSchedule schedule, LocalDate lastLessonDate) {
//        Group group = schedule.getGroup();
//        LocalDate startDate = lastLessonDate != null ? lastLessonDate : LocalDate.now();
//        LocalDate endDate = startDate.plusMonths(group.getCourse().getDuration());
//
//        Integer lastLessonNumber = lessonInstanceRepository.findMaxLessonNumberByGroupId(group.getId());
//        int lessonNumber = lastLessonNumber != null ? lastLessonNumber + 1 : 1;
//
//        // Build a single INSERT statement with multiple rows
//        List<String> valueRows = new ArrayList<>();
//        LocalDate currentDate = startDate;
//        while (!currentDate.isAfter(endDate)) {
//            int dayOfWeek = currentDate.getDayOfWeek().getValue();
//            if (schedule.getDay().equals(dayOfWeek)) {
//                String row = String.format("(%d, '%s', %d)",
//                        schedule.getId(), currentDate.toString(), lessonNumber++);
//                valueRows.add(row);
//            }
//            currentDate = currentDate.plusDays(1);
//        }
//
//        if (!valueRows.isEmpty()) {
//            String insertQuery = "INSERT INTO lesson_instances (lesson_schedule_id, lesson_date, lesson_number) VALUES "
//                                 + String.join(", ", valueRows);
//            jdbcTemplate.update(insertQuery);
//        }
//    }
}