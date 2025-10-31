package uz.shuhrat.lms.db.repository.exam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByExamId(Long examId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.exam.id = :examId")
    long countByExamId(@Param("examId") Long examId);

    void deleteByExamId(Long examId);
}