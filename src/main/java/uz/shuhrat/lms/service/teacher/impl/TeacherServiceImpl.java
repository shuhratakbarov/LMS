package uz.shuhrat.lms.service.teacher.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.projection.TeacherGroupSummaryProjection;
import uz.shuhrat.lms.projection.TeacherHomeworkListProjection;
import uz.shuhrat.lms.db.domain.Homework;
import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.db.repository.student.HomeworkRepository;
import uz.shuhrat.lms.db.repository.teacher.TeacherRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.EvaluateHomeworkRequestDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.teacher.TeacherService;

import java.util.Optional;
import java.util.UUID;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;
    private final HomeworkRepository homeworkRepository;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository, GroupRepository groupRepository, HomeworkRepository homeworkRepository) {
        this.teacherRepository = teacherRepository;
        this.groupRepository = groupRepository;
        this.homeworkRepository = homeworkRepository;
    }

    @Override
    public GeneralResponseDto<?> getGroups(String keyword, int page, int size) throws Exception {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Authentifikatsiyadan o'ting!");
        }
        if (currentUser.isActive() && currentUser.getRole() == Role.TEACHER) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<TeacherGroupSummaryProjection> groupSummaryProjections;
            try {
                groupSummaryProjections = teacherRepository.getGroups(currentUser.getId(), keyword, pageable);
            } catch (Exception e) {
                System.err.println("Teacher Service getGroups method: " + e.getMessage());
                return new GeneralResponseDto<>(false, e.getMessage());
            }
            return new GeneralResponseDto<>(true, "ok", groupSummaryProjections);
        }
        return new GeneralResponseDto<>(false, "Group ro'yxatini olishga ruxsat yo'q!!!");
    }

    @Override
    public GeneralResponseDto<?> getHomeworkList(Long groupId, String taskId, int page, int size) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (optionalGroup.isEmpty()) {
                return new GeneralResponseDto<>(false, "bunday group mavjud emas");
            }
            if (currentUser != null && currentUser.getId().equals(optionalGroup.get().getTeacher().getId())) {
                Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
                Page<TeacherHomeworkListProjection> homeworkListDtoPage= teacherRepository.getHomeworkByTaskId(UUID.fromString(taskId), pageable);
                return new GeneralResponseDto<>(true, "ok", homeworkListDtoPage);
            }
            return new GeneralResponseDto<>(false, "Group studentlarini olishga ruxmat yo'q!!!");
        } catch (Exception e) {
            System.err.println("Teacher Service getStudentOfGroup method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> evaluateHomework(UUID homeworkId, EvaluateHomeworkRequestDto evaluateHomeworkRequestDto) {
        try {
            Optional<Homework> optionalHomework = homeworkRepository.findById(homeworkId);
            if (optionalHomework.isPresent()) {
                Homework homework = optionalHomework.get();
                homework.setBall(evaluateHomeworkRequestDto.homeworkBall());
                homework.setDescription(evaluateHomeworkRequestDto.description());
                homeworkRepository.save(homework);
                return new GeneralResponseDto<>(true, "Homework evaluated successfully");
            }
            throw new Exception("Homework doesn't exist");
        } catch (Exception e) {
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }
}
