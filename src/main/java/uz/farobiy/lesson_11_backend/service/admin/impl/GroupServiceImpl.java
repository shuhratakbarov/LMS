package uz.farobiy.lesson_11_backend.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.farobiy.lesson_11_backend.db.domain.Course;
import uz.farobiy.lesson_11_backend.db.domain.Group;
import uz.farobiy.lesson_11_backend.db.domain.User;
import uz.farobiy.lesson_11_backend.db.domain.customDto.admin.GroupCustomDto;
import uz.farobiy.lesson_11_backend.db.domain.customDto.admin.GroupIdAndName;
import uz.farobiy.lesson_11_backend.db.repository.admin.CourseRepository;
import uz.farobiy.lesson_11_backend.db.repository.admin.GroupRepository;
import uz.farobiy.lesson_11_backend.db.repository.admin.UserRepository;
import uz.farobiy.lesson_11_backend.dto.GroupResponseDto;
import uz.farobiy.lesson_11_backend.dto.PageDataResponseDto;
import uz.farobiy.lesson_11_backend.dto.ResponseDto;
import uz.farobiy.lesson_11_backend.dto.form.CreateGroupForm;
import uz.farobiy.lesson_11_backend.service.admin.GroupService;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private final GroupRepository groupRepository;
    @Autowired
    private final CourseRepository courseRepository;
    @Autowired
    private final UserRepository userRepository;


    public GroupServiceImpl(GroupRepository groupRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseDto save(CreateGroupForm form) throws Exception {
        try {
            if (form == null || form.getName() == null || form.getCourseId() == null) {
                throw new Exception("Ma'lumotlar to'liq emas!!!");
            }
            Optional<Course> cOp = courseRepository.findById(form.getCourseId());
            if (!cOp.isPresent()) {
                throw new Exception("Bunday course mavjud emas!!");
            }
            Optional<User> uOp= userRepository.findById(form.getTeacherId());
            if (!uOp.isPresent()) {
                throw new Exception("Bunday teacher mavjud emas!!");
            }
            Group g = new Group();
            g.setName(form.getName());
            g.setDescription(form.getDescription());
            g.setCourse(cOp.get());
            g.setTeacher(uOp.get());

            g = groupRepository.save(g);

            if (g == null) {
                throw new Exception("Saqlanmadi");
            }
            return new ResponseDto<>(true,"ok");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto edit(Long id, CreateGroupForm form) throws Exception {
        try {
            if (form == null || form.getName() == null || form.getCourseId() == null) {
                throw new Exception("Ma'lumotlar to'liq emas!!!");
            }
//            Optional<Course> cOp = courseRepository.findById(form.getCourseId());
//            if (!cOp.isPresent()) {
//                throw new Exception("Bunday course mavjud emas!!");
//            }
            Optional<User> uOp = userRepository.findById(form.getTeacherId());
            if (!uOp.isPresent()){
                throw new Exception("Teacher topilmadi");
            }
            Optional<Group> gOp=groupRepository.findById(id);
            if (!gOp.isPresent()){
                throw new Exception("Grux topilmadi");
            }
            Group g = gOp.get();
//            g.setCourse(cOp.get());
            g.setName(form.getName());
            g.setDescription(form.getDescription());

            g = groupRepository.save(g);

            if (g == null) {
                throw new Exception("Saqlanmadi");
            }
            return new ResponseDto<>(true,"ok");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto delete(Long id) throws Exception {
        try {

            Optional<Group> group = groupRepository.findById(id);
            if (group.isEmpty()) {
                return new ResponseDto<>(false, "group not found");
            }
            Optional<Long> count = userRepository.countStudentsByGroupId(id);
                if (count.get() > 0) {
                    return new ResponseDto<>(false, "Bu guruhda "+count.get()+" ta student mavjud, dastlab u(lar)ni guruhdan chiqarishingiz kerak?", count.get());
                }
            groupRepository.deleteById(id);
            return new ResponseDto<>(true, "o'chirildi");
        }catch (Exception e){
           e.printStackTrace();
return new ResponseDto<>(false,e.getMessage());
       }
    }


    @Override
    public ResponseDto search(String searching, int page, int size) throws Exception {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Group> pages = groupRepository.search(searching,pageable);
        List<GroupResponseDto> list = pages.getContent().stream().map(group ->
                        new GroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getCourse().getName(),group.getTeacher().getUsername(),  group.getCreateAt(), group.getUpdateAt()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<GroupResponseDto>> dto = new PageDataResponseDto(list,pages.getTotalElements());
        return new ResponseDto<>(true,"ok",dto);
    }
    @Override
    public ResponseDto findAll(int page, int size) throws Exception {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Group> pages = groupRepository.findAll(pageable);
        List<GroupResponseDto> list = pages.getContent().stream().map(group ->
                        new GroupResponseDto(group.getId(), group.getName(),group.getDescription(), group.getCourse().getName(),group.getTeacher().getUsername(), (Date) group.getCreateAt(), (Date) group.getUpdateAt()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<GroupResponseDto>> dto = new PageDataResponseDto(list,pages.getTotalElements());
        return new ResponseDto<>(true,"ok",dto);
    }

    @Override
    public ResponseDto findGroupsByTeacherId(UUID teacherId) throws Exception {
        return new ResponseDto<>(true, "ok", groupRepository.getGroupsAndCountByTeacherId(teacherId));
    }

    @Override
    public ResponseDto getGroupsAndTeacherByStudentId(UUID userId) throws Exception {
        return new ResponseDto<>(true, "ok", groupRepository.getGroupsAndTeacherByStudentId(userId));
    }

    @Override
    public ResponseDto findAllByCourseId(Long courseId, int page, int size) throws Exception {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Group> pages = groupRepository.findAllByCourseId(courseId,pageable);
        List<GroupResponseDto> list = pages.getContent().stream().map(group ->
                        new GroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getCourse().getName(),group.getTeacher().getUsername(), (Date) group.getCreateAt(), (Date) group.getUpdateAt()))
                .collect(Collectors.toList());
        PageDataResponseDto<List<GroupResponseDto>> dto = new PageDataResponseDto(list,pages.getTotalElements());
        return new ResponseDto<>(true,"ok");
    }


    @Override
    public ResponseDto addStudentToGroup(UUID studentId, Long groupId) throws Exception {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isEmpty()){
            return new ResponseDto<>(false,"group topilmadi");
        }

        Optional<User> optionalUser = userRepository.findById(studentId);
        if (optionalUser.isEmpty()){
            return new ResponseDto<>(false,"student topilmadi");
        }
        Group group=optionalGroup.get();
        User user = optionalUser.get();
        List<User> users = group.getStudents();
        users.add(user);
        group.setStudents(users);
        groupRepository.save(group);
//        yoki
//        groupRepository.addStudentToGroup(studentId, groupId);
        return new ResponseDto<>(true,"ok");
    }

    @Override
    public ResponseDto removeStudentFromGroup(UUID studentId, Long groupId) {
        groupRepository.removeStudentFromGroupByStudentId(studentId, groupId);
        return new ResponseDto<>(true, "Student removed from group successfully");
    }

//    @Override
//    public ResponseDto groupsList() {
//        try {
//            List<GroupCustomDto> list=groupRepository.getGroupsList();
//            return new ResponseDto(true,"ok",list);
//        }catch (Exception e){
//            e.printStackTrace();
//            return new ResponseDto(false,e.getMessage());
//        }
//    }

    @Override
    public ResponseDto getGroupIdAndName() {
        try {
            List<GroupIdAndName> groupIdAndNames = groupRepository.getGroupIdAndName();
            return new ResponseDto<>(true, "ok", groupIdAndNames);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, e.getMessage());
        }
    }
}
