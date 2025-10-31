package uz.shuhrat.lms.db.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "exam_answers")
public class ExamAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attendance_id")
    private ExamAttendance attendance;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String selectedAnswer;
}
