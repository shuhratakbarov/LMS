package uz.shuhrat.lms.db.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.shuhrat.lms.enums.ExamStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String description;

    @Enumerated(EnumType.STRING)
    private ExamStatus status;

    private String difficulty;
    private String areas; // comma separated
    private Integer duration; // in minutes
    private Instant deadline;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
}
