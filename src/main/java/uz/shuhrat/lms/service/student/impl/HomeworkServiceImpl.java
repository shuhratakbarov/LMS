package uz.shuhrat.lms.service.student.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.db.domain.File;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.domain.Homework;
import uz.shuhrat.lms.db.domain.Task;
import uz.shuhrat.lms.db.repository.student.HomeworkRepository;
import uz.shuhrat.lms.db.repository.teacher.TaskRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.file.FileService;
import uz.shuhrat.lms.service.student.HomeworkService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class HomeworkServiceImpl implements HomeworkService {
    private final FileService fileService;
    private final HomeworkRepository homeworkRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public HomeworkServiceImpl(FileService fileService, HomeworkRepository homeworkRepository, TaskRepository taskRepository) {
        this.fileService = fileService;
        this.homeworkRepository = homeworkRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public GeneralResponseDto<?> saveHomework(UUID taskId, UUID homeworkId, MultipartFile file) {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser == null) return new GeneralResponseDto<>(false, "Current user doesn't exist");
        if (taskId == null) return new GeneralResponseDto<>(false, "Task id is not presented");
        if (file.isEmpty()) return new GeneralResponseDto<>(false, "File is empty");
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) return new GeneralResponseDto<>(false, "Task doesn't exist");
        Task task = taskOptional.get();
        if (task.getDeadline().isBefore(LocalDateTime.now())) return new GeneralResponseDto<>(false, "Deadline expired!");
        GeneralResponseDto<?> generalResponseDto = fileService.save(file);
        if (!generalResponseDto.isSuccess()) return new GeneralResponseDto<>(false, "An error occurred while saving the file");
        Homework homework = new Homework();
        if (homeworkId == null) {
            homework.setTask(task);
            homework.setStudent(currentUser);
            homework.setFile((File) generalResponseDto.getData());
            homework = homeworkRepository.save(homework);
            if (homework.getId() == null)
                return new GeneralResponseDto<>(false, "An error occurred while saving the homework!");
            return new GeneralResponseDto<>(true, "Homework saved successfully");
        }
        Optional<Homework> optionalHomework = homeworkRepository.findById(homeworkId);
        if (optionalHomework.isEmpty()) return new GeneralResponseDto<>(false, "Homework doesn't exist");
        homework = optionalHomework.get();
        String previousFileName = homework.getFile().getName();
        String previousFilePkey = homework.getFile().getPkey();
        homework.setFile((File) generalResponseDto.getData());
        homework = homeworkRepository.save(homework);
        if (homework.getId() == null) return new GeneralResponseDto<>(false, "An error occurred while updating the homework");
        fileService.delete(previousFilePkey, previousFileName);
        return new GeneralResponseDto<>(true, "Homework updated successfully");
    }
}
