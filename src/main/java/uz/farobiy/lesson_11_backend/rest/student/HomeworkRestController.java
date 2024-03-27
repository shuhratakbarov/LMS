package uz.farobiy.lesson_11_backend.rest.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.farobiy.lesson_11_backend.db.domain.tasks.Homework;
import uz.farobiy.lesson_11_backend.service.student.HomeworkService;


import java.util.UUID;

@RestController
@RequestMapping("/student/homework")
public class HomeworkRestController {
    @Autowired
    private final HomeworkService homeworkService;

    public HomeworkRestController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }






}
