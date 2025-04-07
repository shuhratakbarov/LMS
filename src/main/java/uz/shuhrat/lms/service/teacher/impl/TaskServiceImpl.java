package uz.shuhrat.lms.service.teacher.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.db.customDto.teacher.GroupCustomForTeacher;
import uz.shuhrat.lms.db.domain.*;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.db.repository.teacher.TaskRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.db.repository.teacher.TeacherRepository;
import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.form.CreateTaskForm;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.file.FileService;
import uz.shuhrat.lms.service.teacher.TaskService;

import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, FileService fileService, UserRepository userRepository, GroupRepository groupRepository, TeacherRepository teacherRepository) {
        this.taskRepository = taskRepository;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public ResponseDto<?> saveTask(MultipartFile multipartFile, CreateTaskForm form) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                throw new Exception("current user not found");
            }

            if (form.getDeadline().before(new Date())) {
                throw new Exception("Talabalarni qiynamang:)!");
            }
            if (!multipartFile.isEmpty() && form.getDeadline() != null && form.getMaxBall() != null && form.getName() != null && form.getType() != null) {

                Optional<Group> optionalGroup = groupRepository.findById(Long.parseLong(form.getGroupId()));
                if (optionalGroup.isEmpty()) {
                    return new ResponseDto<>(false, "Group topilmadi!!!");
                }
//                if (currentUser.getId() != optionalGroup.get().getTeacher().getId()) {
//                    throw new Exception("sizga mumkin emas");
//                }
                File file = (File) fileService.save(multipartFile).getData();
                Task task = new Task();
                Group group = optionalGroup.get();
                task.setName(form.getName());
                task.setType(form.getType());
                task.setGroup(group);
                task.setDeadline(form.getDeadline());
                task.setMaxBall(form.getMaxBall());
                task.setFile(file);
                task = taskRepository.save(task);
                if (task.getId() != null && task.getFile() != null) {
                    return new ResponseDto<>(true, "Task yaratildi");
                }
                return new ResponseDto<>(false, "saqlashda xatolik yuz berdi");
            }
            return new ResponseDto<>(false, "Ma'lumotlar to'liq emas!");
        } catch (Exception e) {
            System.err.println("Task Service saveTask method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> editTask(UUID taskId, CreateTaskForm form, MultipartFile multipartFile) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                return new ResponseDto<>(false, "User topilmadi!!!");
            }
            Optional<Group> optionalGroup = groupRepository.findById(taskRepository.findById(taskId).get().getGroup().getId());
            if (optionalGroup.isEmpty()) {
                return new ResponseDto<>(false, "Group topilmadi!!!");
            }
//            if (currentUser.getId() != optionalGroup.get().getTeacher().getId()) {
//                return new ResponseDto<>(false, "Sizga mumkin emas!");
//            }
            Optional<Task> optionalTask = taskRepository.findById(taskId);
            if (optionalTask.isEmpty()) {
                return new ResponseDto<>(false, "Task topilmadi!!!");
            }
            if (optionalTask.get().getFile().getPkey() != null) {
                fileService.delete(optionalTask.get().getFile().getPkey(), optionalTask.get().getFile().getName());
            }
            File file;
            if (!multipartFile.isEmpty()) {
                file = (File) fileService.save(multipartFile).getData();
            } else {
                return new ResponseDto<>(false, "Fayl yuklanmadi");
            }
            Task task = new Task();
            task.setId(taskId);
            task.setName(form.getName());
            task.setType(form.getType());
            task.setGroup(optionalGroup.get());
            task.setFile(file);
            task.setDeadline(form.getDeadline());
            task.setMaxBall(form.getMaxBall());
            taskRepository.save(task);
            return new ResponseDto<>(true, "Task o'zgartirildi");
        } catch (Exception e) {
            System.err.println("Task Service editTask method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> deleteTask(UUID taskId) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                return new ResponseDto<>(false, "User topilmadi!!!");
            }
            Optional<Task> optionalTask = taskRepository.findById(taskId);
//            if (currentUser.getId() == groupRepository.findById(optionalTask.get().getGroup().getId()).get().getTeacher().getId()) {
                if (optionalTask.get().getFile().getPkey() != null) {
                    fileService.delete(optionalTask.get().getFile().getPkey(), optionalTask.get().getFile().getName());
                }
                taskRepository.delete(optionalTask.get());
                return new ResponseDto<>(true, "Task o'chirildi");
//            } else {
//                return new ResponseDto<>(false, "Sizga mumkin emas!");
//            }
        } catch (Exception e) {
            System.err.println("Task Service deleteTask method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> findAllWithFiles(Long groupId) {
        return new ResponseDto<>(true, "ok", taskRepository.getGroupTasks(groupId));
    }

}
