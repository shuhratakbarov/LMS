package uz.shuhrat.lms.service.teacher.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.customDto.teacher.GroupCustomForTeacher;
import uz.shuhrat.lms.db.customDto.teacher.HomeworkListDto;
import uz.shuhrat.lms.db.domain.Homework;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.db.repository.student.HomeworkRepository;
import uz.shuhrat.lms.db.repository.teacher.TeacherRepository;
import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.form.EvaluateHomework;
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
    public ResponseDto<?> getGroups(String keyword, int page, int size) {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.isActive() && currentUser.getRole().getName().equals("ROLE_TEACHER")) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<GroupCustomForTeacher> groupCustomForTeacherPage;
            try {
                groupCustomForTeacherPage = teacherRepository.getGroups(currentUser.getId(), keyword, pageable);
            } catch (Exception e) {
                System.err.println("Teacher Service getGroups method: " + e.getMessage());
                return new ResponseDto<>(false, e.getMessage());
            }
            return new ResponseDto<>(true, "ok", groupCustomForTeacherPage);
        }
        return new ResponseDto<>(false, "Group ro'yxatini olishga ruxsat yo'q!!!");
    }

    @Override
    public ResponseDto<?> getHomeworkList(Long groupId, String taskId, int page, int size) throws Exception {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (optionalGroup.isEmpty()) {
                return new ResponseDto<>(false, "bunday group mavjud emas");
            }
            if (currentUser != null && currentUser.getId().equals(optionalGroup.get().getTeacher().getId())) {
                Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
                Page<HomeworkListDto> homeworkListDtoPage= teacherRepository.getHomeworkByTaskId(UUID.fromString(taskId), pageable);
                return new ResponseDto<>(true, "ok", homeworkListDtoPage);
            }
            return new ResponseDto<>(false, "Group studentlarini olishga ruxmat yo'q!!!");
        } catch (Exception e) {
            System.err.println("Teacher Service getStudentOfGroup method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> evaluateHomework(UUID homeworkId, EvaluateHomework evaluateHomework) {
        try {
            Optional<Homework> optionalHomework = homeworkRepository.findById(homeworkId);
            if (optionalHomework.isPresent()) {
                Homework homework = optionalHomework.get();
                homework.setBall(evaluateHomework.getHomeworkBall());
                homework.setDescription(evaluateHomework.getDescription());
                homework = homeworkRepository.save(homework);
                if (homework.getBall() != null) {
                    return new ResponseDto<>(true, "Homework evaluated successfully");
                }
                throw new Exception("An error occurred while evaluating the homework");
            }
            throw new Exception("Homework doesn't exist");
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }
    }
}
