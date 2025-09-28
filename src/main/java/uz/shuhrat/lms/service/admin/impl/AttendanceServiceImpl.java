package uz.shuhrat.lms.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.shuhrat.lms.db.domain.Attendance;
import uz.shuhrat.lms.db.domain.LessonInstance;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.AttendanceRepository;
import uz.shuhrat.lms.db.repository.LessonInstanceRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.request.AttendanceRequestDto;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.MarkAttendanceRequestDto;
import uz.shuhrat.lms.service.admin.AttendanceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final LessonInstanceRepository lessonInstanceRepository;
    private final UserRepository userRepository;

    @Autowired
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, LessonInstanceRepository lessonInstanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.lessonInstanceRepository = lessonInstanceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public GeneralResponseDto<?> markAttendance(MarkAttendanceRequestDto markAttendanceRequestDto) throws Exception {
        LessonInstance lessonInstance = lessonInstanceRepository.findById(markAttendanceRequestDto.lessonInstanceId())
                .orElseThrow(() -> new RuntimeException("Lesson instance not found: " + markAttendanceRequestDto.lessonInstanceId()));

        List<AttendanceRequestDto> attendanceRequestDtoList = markAttendanceRequestDto.attendanceRequestDtoList();

        // Step 1: Check student existence in bulk
        List<UUID> studentIds = attendanceRequestDtoList.stream()
                .map(AttendanceRequestDto::studentId)
                .collect(Collectors.toList());
        List<User> students = userRepository.findAllById(studentIds);
        Map<UUID, User> studentMap = students.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        for (AttendanceRequestDto dto : attendanceRequestDtoList) {
            if (!studentMap.containsKey(dto.studentId())) {
                throw new RuntimeException("Student not found: " + dto.studentId());
            }
        }

        // Step 2: Fetch existing attendance records in bulk
        List<Attendance> existingAttendances = attendanceRepository.findByLessonInstanceIdAndStudentIds(
                markAttendanceRequestDto.lessonInstanceId(), studentIds);
        Map<UUID, Attendance> existingAttendanceMap = existingAttendances.stream()
                .collect(Collectors.toMap(att -> att.getStudent().getId(), att -> att));

        // Step 3: Validate and prepare Attendance entities (update or create)
        List<Attendance> attendancesToSave = new ArrayList<>();
        for (AttendanceRequestDto dto : attendanceRequestDtoList) {
            Boolean isPresent = dto.isPresent();
            Integer minutesLate = getMinutesLate(dto, isPresent);

            Attendance attendance = existingAttendanceMap.getOrDefault(dto.studentId(), new Attendance());
            attendance.setLessonInstance(lessonInstance);
            attendance.setStudent(studentMap.get(dto.studentId()));
            attendance.setIsPresent(isPresent);
            attendance.setMinutesLate(minutesLate);
            attendancesToSave.add(attendance);
        }

        // Step 4: Save all attendances (creates new or updates existing)
        return new GeneralResponseDto<>(true, "ok", attendanceRepository.saveAll(attendancesToSave));
    }

    private static Integer getMinutesLate(AttendanceRequestDto dto, Boolean isPresent) throws Exception {
        int minutesLate = dto.minutesLate();

        if (isPresent == null) {
            throw new Exception("Attendance status (is_present) must be specified for student " + dto.studentId());
        }
        if (minutesLate < 0) {
            throw new Exception("Minutes late cannot be negative for student " + dto.studentId());
        }
        if (!isPresent && minutesLate > 0) {
            throw new Exception("Minutes late should be 0 if the student is absent for student " + dto.studentId());
        }
        return minutesLate;
    }

    public GeneralResponseDto<?> getAttendanceByLessonInstance(Long lessonInstanceId) {
        return new GeneralResponseDto<>(true, "ok", attendanceRepository.findAll().stream()
                .filter(att -> att.getLessonInstance().getId().equals(lessonInstanceId))
                .toList());
    }
}