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
import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.file.FileService;
import uz.shuhrat.lms.service.student.HomeworkService;

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
    public ResponseDto<?> saveHomework(UUID taskId, UUID homeworkId, MultipartFile file) {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser == null) return new ResponseDto<>(false, "Current user doesn't exist");
        if (taskId == null) return new ResponseDto<>(false, "Task id is not presented");
        if (file.isEmpty()) return new ResponseDto<>(false, "File is empty");
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) return new ResponseDto<>(false, "Task doesn't exist");
        Task task = taskOptional.get();
        if (task.getDeadline().before(new Date())) return new ResponseDto<>(false, "Deadline expired!");
        ResponseDto<?> responseDto = fileService.save(file);
        if (!responseDto.isSuccess()) return new ResponseDto<>(false, "An error occurred while saving the file");
        Homework homework = new Homework();
        if (homeworkId == null) {
            homework.setTask(task);
            homework.setStudent(currentUser);
            homework.setFile((File) responseDto.getData());
            homework = homeworkRepository.save(homework);
            if (homework.getId() == null)
                return new ResponseDto<>(false, "An error occurred while saving the homework!");
            return new ResponseDto<>(true, "Homework saved successfully");
        }
        Optional<Homework> optionalHomework = homeworkRepository.findById(homeworkId);
        if (optionalHomework.isEmpty()) return new ResponseDto<>(false, "Homework doesn't exist");
        homework = optionalHomework.get();
        String previousFileName = homework.getFile().getName();
        String previousFilePkey = homework.getFile().getPkey();
        homework.setFile((File) responseDto.getData());
        homework = homeworkRepository.save(homework);
        if (homework.getId() == null) return new ResponseDto<>(false, "An error occurred while updating the homework");
        fileService.delete(previousFilePkey, previousFileName);
        return new ResponseDto<>(true, "Homework updated successfully");
    }
}
