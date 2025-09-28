package uz.shuhrat.lms.service.teacher.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.db.domain.*;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.db.repository.teacher.TaskRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.db.repository.teacher.TeacherRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.TaskRequestDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.file.FileService;
import uz.shuhrat.lms.service.teacher.TaskService;

import java.time.LocalDateTime;
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
    public GeneralResponseDto<?> saveTask(MultipartFile multipartFile, TaskRequestDto form) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                throw new Exception("current user not found");
            }

            if (form.deadline().isBefore(LocalDateTime.now())) {
                throw new Exception("Invalid date");
            }
            if (!multipartFile.isEmpty()) {
                Optional<Group> optionalGroup = groupRepository.findById(Long.parseLong(form.groupId()));
                if (optionalGroup.isEmpty()) {
                    return new GeneralResponseDto<>(false, "Group not found!");
                }
                if (!currentUser.getId().equals(optionalGroup.get().getTeacher().getId())) {
                    throw new Exception("You are not allowed");
                }
                File file = (File) fileService.save(multipartFile).getData();
                Task task = new Task();
                Group group = optionalGroup.get();
                task.setName(form.name());
                task.setType(form.type());
                task.setGroup(group);
                task.setDeadline(form.deadline());
                task.setMaxBall(form.maxBall());
                task.setFile(file);
                taskRepository.save(task);
                return new GeneralResponseDto<>(true, "Task has been created");
            }
            return new GeneralResponseDto<>(false, "No file presented!");
        } catch (Exception e) {
            System.err.println("Task Service saveTask method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> editTask(UUID taskId, TaskRequestDto form, MultipartFile multipartFile) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                return new GeneralResponseDto<>(false, "User not found!");
            }
            Optional<Group> optionalGroup = groupRepository.findById(taskRepository.findById(taskId).get().getGroup().getId());
            if (optionalGroup.isEmpty()) {
                return new GeneralResponseDto<>(false, "Group not found!");
            }
            if (!currentUser.getId().equals(optionalGroup.get().getTeacher().getId())) {
                return new GeneralResponseDto<>(false, "You are not allowed!");
            }
            Optional<Task> optionalTask = taskRepository.findById(taskId);
            if (optionalTask.isEmpty()) {
                return new GeneralResponseDto<>(false, "Task not found!");
            }
            File file;
            Task task = optionalTask.get();
            task.setName(form.name());
            task.setType(form.type());
            task.setGroup(optionalGroup.get());
            if (multipartFile != null) {
                file = (File) fileService.save(multipartFile).getData();
                if (file.getPkey()!=null && optionalTask.get().getFile().getPkey() != null) {
                    task.setFile(file);
                    fileService.delete(optionalTask.get().getFile().getPkey(), optionalTask.get().getFile().getName());
                }
            }
            task.setDeadline(form.deadline());
            task.setMaxBall(form.maxBall());
            taskRepository.save(task);
            return new GeneralResponseDto<>(true, "Task has been updated");
        } catch (Exception e) {
            System.err.println("Task Service editTask method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> deleteTask(UUID taskId) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                return new GeneralResponseDto<>(false, "User not found!");
            }
            Optional<Task> optionalTask = taskRepository.findById(taskId);
            if (currentUser.getId().equals(groupRepository.findById(optionalTask.get().getGroup().getId()).get().getTeacher().getId())) {
                if (optionalTask.get().getFile().getPkey() != null) {
                    fileService.delete(optionalTask.get().getFile().getPkey(), optionalTask.get().getFile().getName());
                }
                taskRepository.delete(optionalTask.get());
                return new GeneralResponseDto<>(true, "Task has been deleted");
            } else {
                return new GeneralResponseDto<>(false, "You are not allowed!");
            }
        } catch (Exception e) {
            System.err.println("Task Service deleteTask method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> findAllWithFiles(Long groupId) {
        return new GeneralResponseDto<>(true, "ok", taskRepository.getGroupTasks(groupId));
    }

}
