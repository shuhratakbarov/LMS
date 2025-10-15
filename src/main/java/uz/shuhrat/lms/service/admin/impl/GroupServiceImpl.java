package uz.shuhrat.lms.service.admin.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.shuhrat.lms.db.domain.*;
import uz.shuhrat.lms.projection.GroupIdAndNameProjection;
import uz.shuhrat.lms.db.repository.admin.CourseRepository;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.response.GroupResponseDto;
import uz.shuhrat.lms.dto.response.PageDataResponseDto;
import uz.shuhrat.lms.dto.request.GroupRequestDto;
import uz.shuhrat.lms.service.admin.GroupService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public GeneralResponseDto<?> save(GroupRequestDto form) {
        try {
            Group group = new Group();
            group.setName(form.name());
            group.setDescription(form.description());
            group.setTeacher(userRepository.findById(form.teacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found")));
            group.setCourse(courseRepository.findById(form.courseId())
                    .orElseThrow(() -> new RuntimeException("Course not found")));
            groupRepository.save(group);

//            GroupChat groupChat = groupChatService.createForGroup(group.getId());
//            if (groupChat.getId() == null) {
//                return new GeneralResponseDto<>(false, "An error occurred while creating chat for the group");
//            }
            return new GeneralResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.err.println("Group Service save method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> getGroupList(String searching, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Group> pages = groupRepository.getGroups(searching, pageable);
        List<GroupResponseDto> list = pages.getContent().stream().map(group ->
                        new GroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getCourse().getName(), group.getTeacher().getUsername(), group.getCreatedAt(), group.getUpdatedAt()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<GroupResponseDto>> dto = new PageDataResponseDto<>(list, pages.getTotalElements());
        return new GeneralResponseDto<>(true, "ok", dto);
    }

    @Override
    public GeneralResponseDto<?> edit(Long id, GroupRequestDto form) {
        try {
            Optional<Group> gOp = groupRepository.findById(id);
            if (gOp.isEmpty()) {
                throw new Exception("Guruh topilmadi");
            }
            Group group = gOp.get();
            group.setName(form.name());
            group.setTeacher(userRepository.findById(form.teacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found")));
            group.setCourse(courseRepository.findById(form.courseId())
                    .orElseThrow(() -> new RuntimeException("Course not found")));
            group.setDescription(form.description());
            groupRepository.save(group);
            return new GeneralResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.out.println("Group Service edit method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> delete(Long id) throws Exception {
        try {
            Optional<Group> group = groupRepository.findById(id);
            if (group.isEmpty()) {
                return new GeneralResponseDto<>(false, "Group not found");
            }
            Optional<Long> count = userRepository.countStudentsByGroupId(id);
            if (count.isPresent()) {
                if (count.get() > 0) {
                    return new GeneralResponseDto<>(false, "Bu guruhda " + count.get() + " ta student mavjud, dastlab u(lar)ni guruhdan chiqarishingiz kerak", count.get());
                }
            } else {
                return new GeneralResponseDto<>(false, "Count is not present");
            }
            groupRepository.deleteById(id);
            return new GeneralResponseDto<>(true, "O'chirildi");
        } catch (Exception e) {
            System.out.println("Group Service delete method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> findGroupsByTeacherId(UUID teacherId) {
        return new GeneralResponseDto<>(true, "ok", groupRepository.getGroupsAndCountByTeacherId(teacherId));
    }

    @Override
    public GeneralResponseDto<?> getGroupsAndTeacherByStudentId(UUID userId) {
        return new GeneralResponseDto<>(true, "ok", groupRepository.getGroupsAndTeacherByStudentId(userId));
    }

    @Override
    public GeneralResponseDto<?> findAllByCourseId(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Group> pages = groupRepository.findAllByCourseId(courseId, pageable);
        List<GroupResponseDto> list = pages.getContent().stream().map(group ->
                        new GroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getCourse().getName(), group.getTeacher().getUsername(), group.getCreatedAt(), group.getUpdatedAt()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<GroupResponseDto>> dto = new PageDataResponseDto<>(list, pages.getTotalElements());
        return new GeneralResponseDto<>(true, "ok", dto);
    }

    @Override
    public GeneralResponseDto<?> addStudentToGroup(UUID studentId, Long groupId) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isEmpty()) {
            return new GeneralResponseDto<>(false, "group topilmadi");
        }
        Optional<User> optionalUser = userRepository.findById(studentId);
        if (optionalUser.isEmpty()) {
            return new GeneralResponseDto<>(false, "student topilmadi");
        }
        Group group = optionalGroup.get();
        User user = optionalUser.get();
        List<User> users = group.getStudents();
        users.add(user);
        group.setStudents(users);
        groupRepository.save(group);
        return new GeneralResponseDto<>(true, "ok");
    }

    @Override
    public GeneralResponseDto<?> removeStudentFromGroup(UUID studentId, Long groupId) {
        groupRepository.removeStudentFromGroupByStudentId(studentId, groupId);
        return new GeneralResponseDto<>(true, "Student removed from group successfully");
    }

    @Override
    public GeneralResponseDto<?> getGroupIdAndName() {
        try {
            List<GroupIdAndNameProjection> groupIdAndNameProjections = groupRepository.getGroupIdAndName();
            return new GeneralResponseDto<>(true, "ok", groupIdAndNameProjections);
        } catch (Exception e) {
            System.out.println("Group Service getGroupIdAndName method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }
}
