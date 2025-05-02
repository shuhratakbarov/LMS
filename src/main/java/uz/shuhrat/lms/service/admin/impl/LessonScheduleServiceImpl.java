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
import uz.shuhrat.lms.db.domain.LessonInstance;
import uz.shuhrat.lms.db.domain.LessonSchedule;
import uz.shuhrat.lms.db.repository.AttendanceRepository;
import uz.shuhrat.lms.db.repository.LessonInstanceRepository;
import uz.shuhrat.lms.db.repository.LessonScheduleRepository;
import uz.shuhrat.lms.db.repository.RoomRepository;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.dto.GroupResponseDto;
import uz.shuhrat.lms.dto.PageDataResponseDto;
import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.LessonScheduleDTO;
import uz.shuhrat.lms.service.admin.LessonScheduleService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonScheduleServiceImpl implements LessonScheduleService {
    private final LessonScheduleRepository lessonScheduleRepository;
    private final LessonInstanceRepository lessonInstanceRepository;
    private final GroupRepository groupRepository;
    private final RoomRepository roomRepository;
    private final AttendanceRepository attendanceRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LessonScheduleServiceImpl(LessonScheduleRepository lessonScheduleRepository, LessonInstanceRepository lessonInstanceRepository, GroupRepository groupRepository, RoomRepository roomRepository, AttendanceRepository attendanceRepository, JdbcTemplate jdbcTemplate) {
        this.lessonScheduleRepository = lessonScheduleRepository;
        this.lessonInstanceRepository = lessonInstanceRepository;
        this.groupRepository = groupRepository;
        this.roomRepository = roomRepository;
        this.attendanceRepository = attendanceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ResponseDto<?> getAdminSchedule(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<LessonSchedule> pages = lessonScheduleRepository.getLessonSchedule(keyword, pageable);
        List<LessonScheduleDTO> list = pages.getContent().stream().map(ls ->
                        new LessonScheduleDTO(ls.getId(), ls.getGroup().getId(), ls.getGroup().getName(), ls.getDay(), ls.getStartTime(), ls.getEndTime(), ls.getRoom().getId(), ls.getRoom().getName()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<LessonScheduleDTO>> dto = new PageDataResponseDto<>(list, pages.getTotalElements());
        return new ResponseDto<>(true, "ok", dto);
    }

    @Transactional
    @Override
    public ResponseDto<?> createLessonSchedule(LessonScheduleDTO lessonScheduleDTO) throws Exception {

        if (!checkForScheduleConflicts(lessonScheduleDTO, 0L).isSuccess()) {
            return new ResponseDto<>(false, "Occupied");
        }

        LessonSchedule schedule = new LessonSchedule();
        schedule.setGroup(groupRepository.findById(lessonScheduleDTO.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found")));
        schedule.setDay(lessonScheduleDTO.getDay());
        schedule.setStartTime(lessonScheduleDTO.getStartTime());
        schedule.setEndTime(lessonScheduleDTO.getEndTime());
        schedule.setRoom(roomRepository.findById(lessonScheduleDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));

        schedule = lessonScheduleRepository.save(schedule);

//        generateLessonInstances(schedule, LocalDate.now());

        return new ResponseDto<>(true, "ok", schedule);
    }

    @Transactional
    @Override
    public ResponseDto<?> updateLessonSchedule(Long scheduleId, LessonScheduleDTO lessonScheduleDTO) throws Exception {
        LessonSchedule schedule = lessonScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Lesson schedule not found"));

        if (!checkForScheduleConflicts(lessonScheduleDTO, scheduleId).isSuccess()) {
            return new ResponseDto<>(false, "Occupied");
        }

        Group group = groupRepository.findById(lessonScheduleDTO.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        schedule.setGroup(group);
        schedule.setDay(lessonScheduleDTO.getDay());
        schedule.setStartTime(lessonScheduleDTO.getStartTime());
        schedule.setEndTime(lessonScheduleDTO.getEndTime());
        schedule.setRoom(roomRepository.findById(lessonScheduleDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));

//        LocalDate lastLessonDate = attendanceRepository.findTopLessonInstanceByLessonScheduleOrderByLessonDateDesc(schedule)
//                .map(LessonInstance::getLessonDate)
//                .orElse(LocalDate.MIN);
//
//        lessonInstanceRepository.deleteByLessonScheduleIdAndLessonDateAfter(schedule.getId(), lastLessonDate);
//        generateLessonInstances(schedule, lastLessonDate);

        return new ResponseDto<>(true, "ok", schedule);
    }

    @Override
    public ResponseDto<?> deleteLessonSchedule(Long scheduleId) {
        LessonSchedule schedule = lessonScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Lesson schedule not found"));

        lessonScheduleRepository.delete(schedule);
        return new ResponseDto<>(true, "Lesson schedule deleted successfully");
    }

    @Override
    public ResponseDto<?> checkForScheduleConflicts(LessonScheduleDTO scheduleDTO, Long scheduleId) throws Exception {
        Integer day = scheduleDTO.getDay();
        Integer startTime = scheduleDTO.getStartTime();
        Integer endTime = scheduleDTO.getEndTime();
        Long roomId = scheduleDTO.getRoomId();

        List<LessonSchedule> existingSchedules = lessonScheduleRepository.findByRoomId(roomId);
        for (LessonSchedule existing : existingSchedules) {
            if (existing.getId().equals(scheduleId)) continue;
            if (!existing.getDay().equals(day)) continue;

            if (existing.getStartTime() < endTime && startTime < existing.getEndTime()) {
                return new ResponseDto<>(false, "Room " + existing.getRoom().getName() + " is booked from " + existing.getStartTime() + ":00 to " + existing.getEndTime() + ":00 on " + DayOfWeek.of(day) + " by group " + existing.getGroup().getName());
            }
        }
        return new ResponseDto<>(true, "ok");
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