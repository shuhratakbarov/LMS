package uz.shuhrat.lms.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.db.domain.Course;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.customDto.admin.GroupIdAndName;
import uz.shuhrat.lms.db.repository.admin.CourseRepository;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.GroupResponseDto;
import uz.shuhrat.lms.dto.PageDataResponseDto;
import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.form.CreateGroupForm;
import uz.shuhrat.lms.service.admin.GroupService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseDto<?> save(CreateGroupForm form) throws Exception {
        try {
            if (form == null || form.getName() == null || form.getCourseId() == null) {
                throw new Exception("Ma'lumotlar to'liq emas!!!");
            }
            Optional<Course> cOp = courseRepository.findById(form.getCourseId());
            if (cOp.isEmpty()) {
                throw new Exception("Bunday course mavjud emas!!");
            }
            Optional<User> uOp = userRepository.findById(form.getTeacherId());
            if (uOp.isEmpty()) {
                throw new Exception("Bunday teacher mavjud emas!!");
            }
            Group g = new Group();
            g.setName(form.getName());
            g.setDescription(form.getDescription());
            g.setCourse(cOp.get());
            g.setTeacher(uOp.get());
            g = groupRepository.save(g);
            if (g.getId() == null) {
                throw new Exception("Saqlanmadi");
            }
            return new ResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.out.println("Group Service save method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> edit(Long id, CreateGroupForm form) throws Exception {
        try {
            if (form == null || form.getName() == null || form.getCourseId() == null) {
                throw new Exception("Ma'lumotlar to'liq emas!!!");
            }
            Optional<User> uOp = userRepository.findById(form.getTeacherId());
            if (uOp.isEmpty()) {
                throw new Exception("Teacher topilmadi");
            }
            Optional<Group> gOp = groupRepository.findById(id);
            if (gOp.isEmpty()) {
                throw new Exception("Guruh topilmadi");
            }
            Group g = gOp.get();
            g.setName(form.getName());
            g.setDescription(form.getDescription());
            g = groupRepository.save(g);
            if (g.getId() == null) {
                throw new Exception("Saqlanmadi");
            }
            return new ResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.out.println("Group Service edit method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> delete(Long id) throws Exception {
        try {
            Optional<Group> group = groupRepository.findById(id);
            if (group.isEmpty()) {
                return new ResponseDto<>(false, "Group not found");
            }
            Optional<Long> count = userRepository.countStudentsByGroupId(id);
            if (count.isPresent()) {
                if (count.get() > 0) {
                    return new ResponseDto<>(false, "Bu guruhda " + count.get() + " ta student mavjud, dastlab u(lar)ni guruhdan chiqarishingiz kerak", count.get());
                }
            } else {
                return new ResponseDto<>(false, "Count is not present");
            }
            groupRepository.deleteById(id);
            return new ResponseDto<>(true, "O'chirildi");
        } catch (Exception e) {
            System.out.println("Group Service delete method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }


    @Override
    public ResponseDto<?> search(String searching, int page, int size) throws Exception {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Group> pages = groupRepository.search(searching, pageable);
        List<GroupResponseDto> list = pages.getContent().stream().map(group ->
                        new GroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getCourse().getName(), group.getTeacher().getUsername(), group.getCreateAt(), group.getUpdateAt()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<GroupResponseDto>> dto = new PageDataResponseDto<>(list, pages.getTotalElements());
        return new ResponseDto<>(true, "ok", dto);
    }

    @Override
    public ResponseDto<?> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Group> pages = groupRepository.findAll(pageable);
        List<GroupResponseDto> list = pages.getContent().stream().map(group ->
                        new GroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getCourse().getName(), group.getTeacher().getUsername(), group.getCreateAt(), group.getUpdateAt()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<GroupResponseDto>> dto = new PageDataResponseDto<>(list, pages.getTotalElements());
        return new ResponseDto<>(true, "ok", dto);
    }

    @Override
    public ResponseDto<?> findGroupsByTeacherId(UUID teacherId) {
        return new ResponseDto<>(true, "ok", groupRepository.getGroupsAndCountByTeacherId(teacherId));
    }

    @Override
    public ResponseDto<?> getGroupsAndTeacherByStudentId(UUID userId) {
        return new ResponseDto<>(true, "ok", groupRepository.getGroupsAndTeacherByStudentId(userId));
    }

    @Override
    public ResponseDto<?> findAllByCourseId(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Group> pages = groupRepository.findAllByCourseId(courseId, pageable);
        List<GroupResponseDto> list = pages.getContent().stream().map(group ->
                        new GroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getCourse().getName(), group.getTeacher().getUsername(), group.getCreateAt(), group.getUpdateAt()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<GroupResponseDto>> dto = new PageDataResponseDto<>(list, pages.getTotalElements());
        return new ResponseDto<>(true, "ok", dto);
    }


    @Override
    public ResponseDto<?> addStudentToGroup(UUID studentId, Long groupId) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isEmpty()) {
            return new ResponseDto<>(false, "group topilmadi");
        }
        Optional<User> optionalUser = userRepository.findById(studentId);
        if (optionalUser.isEmpty()) {
            return new ResponseDto<>(false, "student topilmadi");
        }
        Group group = optionalGroup.get();
        User user = optionalUser.get();
        List<User> users = group.getStudents();
        users.add(user);
        group.setStudents(users);
        groupRepository.save(group);
        return new ResponseDto<>(true, "ok");
    }

    @Override
    public ResponseDto<?> removeStudentFromGroup(UUID studentId, Long groupId) {
        groupRepository.removeStudentFromGroupByStudentId(studentId, groupId);
        return new ResponseDto<>(true, "Student removed from group successfully");
    }

    @Override
    public ResponseDto<?> getGroupIdAndName() {
        try {
            List<GroupIdAndName> groupIdAndNames = groupRepository.getGroupIdAndName();
            return new ResponseDto<>(true, "ok", groupIdAndNames);
        } catch (Exception e) {
            System.out.println("Group Service getGroupIdAndName method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }
}
