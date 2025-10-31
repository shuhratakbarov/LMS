package uz.shuhrat.lms.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.shuhrat.lms.db.domain.Exam;
import uz.shuhrat.lms.db.repository.exam.ExamRepository;
import uz.shuhrat.lms.enums.ExamStatus;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExamScheduler {

    private final ExamRepository examRepository;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void autoCompleteExpiredExams() {
        Instant now = Instant.now();
        List<Exam> activeExams = examRepository.findByStatus(ExamStatus.ACTIVE);

        for (Exam exam : activeExams) {
            if (exam.getDeadline() != null && now.isAfter(exam.getDeadline())) {
                exam.setStatus(ExamStatus.COMPLETED);
                examRepository.save(exam);
            }
        }
    }
}
