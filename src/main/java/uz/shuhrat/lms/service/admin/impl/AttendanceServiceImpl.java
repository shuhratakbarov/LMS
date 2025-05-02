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
import uz.shuhrat.lms.dto.AttendanceDTO;
import uz.shuhrat.lms.dto.MarkAttendanceDTO;
import uz.shuhrat.lms.dto.ResponseDto;
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
    public ResponseDto<?> markAttendance(MarkAttendanceDTO markAttendanceDTO) throws Exception {
        LessonInstance lessonInstance = lessonInstanceRepository.findById(markAttendanceDTO.getLessonInstanceId())
                .orElseThrow(() -> new RuntimeException("Lesson instance not found: " + markAttendanceDTO.getLessonInstanceId()));

        List<AttendanceDTO> attendanceDTOs = markAttendanceDTO.getAttendanceDTOS();

        // Step 1: Check student existence in bulk
        List<UUID> studentIds = attendanceDTOs.stream()
                .map(AttendanceDTO::getStudentId)
                .collect(Collectors.toList());
        List<User> students = userRepository.findAllById(studentIds);
        Map<UUID, User> studentMap = students.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        for (AttendanceDTO dto : attendanceDTOs) {
            if (!studentMap.containsKey(dto.getStudentId())) {
                throw new RuntimeException("Student not found: " + dto.getStudentId());
            }
        }

        // Step 2: Fetch existing attendance records in bulk
        List<Attendance> existingAttendances = attendanceRepository.findByLessonInstanceIdAndStudentIds(
                markAttendanceDTO.getLessonInstanceId(), studentIds);
        Map<UUID, Attendance> existingAttendanceMap = existingAttendances.stream()
                .collect(Collectors.toMap(att -> att.getStudent().getId(), att -> att));

        // Step 3: Validate and prepare Attendance entities (update or create)
        List<Attendance> attendancesToSave = new ArrayList<>();
        for (AttendanceDTO dto : attendanceDTOs) {
            Boolean isPresent = dto.isPresent();
            Integer minutesLate = dto.getMinutesLate();

            if (isPresent == null) {
                throw new Exception("Attendance status (is_present) must be specified for student " + dto.getStudentId());
            }
            if (minutesLate < 0) {
                throw new Exception("Minutes late cannot be negative for student " + dto.getStudentId());
            }
            if (!isPresent && minutesLate > 0) {
                throw new Exception("Minutes late should be 0 if the student is absent for student " + dto.getStudentId());
            }

            Attendance attendance = existingAttendanceMap.getOrDefault(dto.getStudentId(), new Attendance());
            attendance.setLessonInstance(lessonInstance);
            attendance.setStudent(studentMap.get(dto.getStudentId()));
            attendance.setIsPresent(isPresent);
            attendance.setMinutesLate(minutesLate);
            attendancesToSave.add(attendance);
        }

        // Step 4: Save all attendances (creates new or updates existing)
        return new ResponseDto<>(true, "ok", attendanceRepository.saveAll(attendancesToSave));
    }

    public ResponseDto<?> getAttendanceByLessonInstance(Long lessonInstanceId) {
        return new ResponseDto<>(true, "ok", attendanceRepository.findAll().stream()
                .filter(att -> att.getLessonInstance().getId().equals(lessonInstanceId))
                .toList());
    }
}