package uz.shuhrat.lms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.shuhrat.lms.db.domain.*;
import uz.shuhrat.lms.db.repository.admin.CourseRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.db.repository.exam.ExamAnswerRepository;
import uz.shuhrat.lms.db.repository.exam.ExamAttendanceRepository;
import uz.shuhrat.lms.db.repository.exam.ExamRepository;
import uz.shuhrat.lms.db.repository.exam.QuestionRepository;
import uz.shuhrat.lms.db.repository.student.StudentRoleRepository;
import uz.shuhrat.lms.dto.request.CreateExamRequestDto;
import uz.shuhrat.lms.dto.request.QuestionRequestDto;
import uz.shuhrat.lms.dto.request.SubmitExamRequestDto;
import uz.shuhrat.lms.dto.request.UpdateExamRequestDto;
import uz.shuhrat.lms.dto.response.*;
import uz.shuhrat.lms.enums.ExamStatus;
import uz.shuhrat.lms.helper.SecurityHelper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamAttendanceRepository attendanceRepository;
    private final ExamAnswerRepository answerRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StudentRoleRepository studentRoleRepository;

    // ==================== TEACHER METHODS ====================

    @Transactional
    public ExamResponseDto createExam(CreateExamRequestDto request) {
        User teacher = userRepository.findById(SecurityHelper.getCurrentUser().getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Exam exam = new Exam();
        exam.setTopic(request.getTopic());
        exam.setDescription(request.getDescription());
        exam.setStatus(ExamStatus.DRAFT);
        exam.setDifficulty(request.getDifficulty());
        exam.setAreas(request.getAreas() != null ? String.join(",", request.getAreas()) : null);
        exam.setDuration(request.getDuration());
        exam.setDeadline(request.getDeadline());
        exam.setCourse(course);
        exam.setTeacher(teacher);
        exam.setCreatedAt(Instant.now());

        Exam savedExam = examRepository.save(exam);

        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            List<Question> questions = request.getQuestions().stream()
                    .map(q -> createQuestion(q, savedExam))
                    .collect(Collectors.toList());

            questionRepository.saveAll(questions);
            savedExam.setQuestions(questions);
        }

        return mapToResponseDto(savedExam);
    }

    @Transactional
    public ExamResponseDto updateExam(Long examId, UpdateExamRequestDto request) {
        Exam exam = getExamByIdAndTeacher(examId, SecurityHelper.getCurrentUser().getId());

        if (exam.getStatus() != ExamStatus.DRAFT) {
            throw new RuntimeException("Can only update exams in DRAFT status");
        }

        exam.setTopic(request.getTopic());
        exam.setDescription(request.getDescription());
        exam.setDifficulty(request.getDifficulty());
        exam.setAreas(request.getAreas() != null ? String.join(",", request.getAreas()) : null);
        exam.setDuration(request.getDuration());
        exam.setDeadline(request.getDeadline());

        if (request.getQuestions() != null) {
            questionRepository.deleteByExamId(examId);
            List<Question> questions = request.getQuestions().stream()
                    .map(q -> createQuestion(q, exam))
                    .collect(Collectors.toList());
            questionRepository.saveAll(questions);
            exam.setQuestions(questions);
        }

        Exam updatedExam = examRepository.save(exam);
        return mapToResponseDto(updatedExam);
    }

    @Transactional
    public void deleteExam(Long examId) {
        Exam exam = getExamByIdAndTeacher(examId, SecurityHelper.getCurrentUser().getId());

        if (exam.getStatus() != ExamStatus.DRAFT) {
            throw new RuntimeException("Can only delete exams in DRAFT status");
        }

        long attendanceCount = attendanceRepository.countSubmittedByExamId(examId);
        if (attendanceCount > 0) {
            throw new RuntimeException("Cannot delete exam with student submissions");
        }

        examRepository.delete(exam);
    }

    @Transactional
    public ExamResponseDto updateExamStatus(Long examId, ExamStatus newStatus) {
        Exam exam = getExamByIdAndTeacher(examId, SecurityHelper.getCurrentUser().getId());
        exam.setStatus(newStatus);
        Exam updatedExam = examRepository.save(exam);
        return mapToResponseDto(updatedExam);
    }

    public ExamDetailsDto getExamDetailsForTeacher(Long examId) {
        Exam exam = getExamByIdAndTeacher(examId, SecurityHelper.getCurrentUser().getId());

        List<QuestionWithAnswerDto> questions = exam.getQuestions().stream()
                .map(q -> QuestionWithAnswerDto.builder()
                        .id(q.getId())
                        .question(q.getQuestion())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .correctAnswer(q.getCorrectAnswer())
                        .build())
                .collect(Collectors.toList());

        return ExamDetailsDto.builder()
                .id(exam.getId())
                .topic(exam.getTopic())
                .description(exam.getDescription())
                .status(exam.getStatus())
                .difficulty(exam.getDifficulty())
                .areas(exam.getAreas() != null ? List.of(exam.getAreas().split(",")) : List.of())
                .duration(exam.getDuration())
                .deadline(exam.getDeadline())
                .courseId(exam.getCourse().getId())
                .courseName(exam.getCourse().getName())
                .createdAt(exam.getCreatedAt())
                .questions(questions)
                .build();
    }

    public ExamStatisticsDto getExamStatistics(Long examId) {
        Exam exam = getExamByIdAndTeacher(examId, SecurityHelper.getCurrentUser().getId());

        List<ExamAttendance> attendances = attendanceRepository.findByExamId(examId);
        long totalStarted = attendances.size();
        long totalSubmitted = attendances.stream().filter(a -> a.getSubmittedAt() != null).count();

        List<Double> scores = new ArrayList<>();
        for (ExamAttendance attendance : attendances) {
            if (attendance.getSubmittedAt() != null) {
                double score = calculateScore(attendance.getId());
                scores.add(score);
            }
        }

        double averageScore = scores.isEmpty() ? 0 : scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double highestScore = scores.isEmpty() ? 0 : scores.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double lowestScore = scores.isEmpty() ? 0 : scores.stream().mapToDouble(Double::doubleValue).min().orElse(0);

        return ExamStatisticsDto.builder()
                .examId(examId)
                .totalStarted(totalStarted)
                .totalSubmitted(totalSubmitted)
                .averageScore(averageScore)
                .highestScore(highestScore)
                .lowestScore(lowestScore)
                .build();
    }

    public List<StudentSubmissionDto> getExamSubmissions(Long examId) {
        Exam exam = getExamByIdAndTeacher(examId, SecurityHelper.getCurrentUser().getId());

        return attendanceRepository.findByExamId(examId).stream()
                .filter(a -> a.getSubmittedAt() != null)
                .map(attendance -> {
                    double score = calculateScore(attendance.getId());
                    return StudentSubmissionDto.builder()
                            .attendanceId(attendance.getId())
                            .studentId(attendance.getStudent().getId())
                            .studentName(attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName())
                            .startedAt(attendance.getStartedAt())
                            .submittedAt(attendance.getSubmittedAt())
                            .score(score)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<ExamResponseDto> getExamsByTeacher() {
        return examRepository.findByTeacherId(SecurityHelper.getCurrentUser().getId()).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // ==================== STUDENT METHODS ====================

    @Transactional
    public AttendanceResponseDto startExam(Long examId) {
        UUID studentId = SecurityHelper.getCurrentUser().getId();
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (exam.getStatus() != ExamStatus.ACTIVE) {
            throw new RuntimeException("Exam is not active");
        }

        if (exam.getDeadline() != null && Instant.now().isAfter(exam.getDeadline())) {
            throw new RuntimeException("Exam deadline has passed");
        }

        if (attendanceRepository.existsByExamIdAndStudentId(examId, studentId)) {
            throw new RuntimeException("You have already started this exam");
        }

        ExamAttendance attendance = new ExamAttendance();
        attendance.setExam(exam);
        attendance.setStudent(student);
        attendance.setStartedAt(Instant.now());

        ExamAttendance savedAttendance = attendanceRepository.save(attendance);
        return mapToAttendanceDto(savedAttendance);
    }

    public ExamWithQuestionsDto continueExam(Long examId) {
        ExamAttendance attendance = attendanceRepository
                .findByExamIdAndStudentId(examId, SecurityHelper.getCurrentUser().getId())
                .orElseThrow(() -> new RuntimeException("You haven't started this exam"));

        if (attendance.getSubmittedAt() != null) {
            throw new RuntimeException("Exam already submitted");
        }

        Exam exam = attendance.getExam();

        // Check if exam has expired
        if (exam.getDeadline() != null && Instant.now().isAfter(exam.getDeadline())) {
            throw new RuntimeException("Exam deadline has passed");
        }

        if (exam.getDuration() != null) {
            Instant expiryTime = attendance.getStartedAt().plusSeconds(exam.getDuration() * 60L);
            if (Instant.now().isAfter(expiryTime)) {
                throw new RuntimeException("Exam time limit has expired");
            }
        }

        // Return exam with questions (same as getExamForStudent)
        List<QuestionDto> questions = exam.getQuestions().stream()
                .map(q -> QuestionDto.builder()
                        .id(q.getId())
                        .question(q.getQuestion())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .build())
                .collect(Collectors.toList());

        return ExamWithQuestionsDto.builder()
                .id(exam.getId())
                .topic(exam.getTopic())
                .description(exam.getDescription())
                .duration(exam.getDuration())
                .deadline(exam.getDeadline())
                .attendanceId(attendance.getId())
                .startedAt(attendance.getStartedAt())
                .questions(questions)
                .build();
    }

    public ExamWithQuestionsDto getExamForStudent(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (exam.getStatus() != ExamStatus.ACTIVE) {
            throw new RuntimeException("Exam is not active");
        }

        ExamAttendance attendance = attendanceRepository
                .findByExamIdAndStudentId(examId, SecurityHelper.getCurrentUser().getId())
                .orElseThrow(() -> new RuntimeException("You need to start the exam first"));

        if (attendance.getSubmittedAt() != null) {
            throw new RuntimeException("Exam already submitted");
        }

        List<QuestionDto> questions = exam.getQuestions().stream()
                .map(q -> QuestionDto.builder()
                        .id(q.getId())
                        .question(q.getQuestion())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .build())
                .collect(Collectors.toList());

        return ExamWithQuestionsDto.builder()
                .id(exam.getId())
                .topic(exam.getTopic())
                .description(exam.getDescription())
                .duration(exam.getDuration())
                .deadline(exam.getDeadline())
                .attendanceId(attendance.getId())
                .startedAt(attendance.getStartedAt())
                .questions(questions)
                .build();
    }

    @Transactional
    public SubmitExamResponseDto submitExam(Long attendanceId, SubmitExamRequestDto request) {
        ExamAttendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        if (!attendance.getStudent().getId().equals(SecurityHelper.getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized: This attendance belongs to another student");
        }

        if (attendance.getSubmittedAt() != null) {
            throw new RuntimeException("Exam already submitted");
        }

        Exam exam = attendance.getExam();

        if (exam.getDeadline() != null && Instant.now().isAfter(exam.getDeadline())) {
            throw new RuntimeException("Exam deadline has passed");
        }

        if (exam.getDuration() != null) {
            Instant maxEndTime = attendance.getStartedAt().plusSeconds(exam.getDuration() * 60L);
            if (Instant.now().isAfter(maxEndTime)) {
                throw new RuntimeException("Exam time limit exceeded");
            }
        }

        List<ExamAnswer> answers = request.getAnswers().stream()
                .map(a -> {
                    Question question = questionRepository.findById(a.getQuestionId())
                            .orElseThrow(() -> new RuntimeException("Question not found"));

                    ExamAnswer answer = new ExamAnswer();
                    answer.setAttendance(attendance);
                    answer.setQuestion(question);
                    answer.setSelectedAnswer(a.getSelectedAnswer());
                    return answer;
                })
                .collect(Collectors.toList());

        answerRepository.saveAll(answers);

        attendance.setSubmittedAt(Instant.now());
        attendanceRepository.save(attendance);

        int correctAnswers = 0;
        for (ExamAnswer answer : answers) {
            if (answer.getSelectedAnswer().equalsIgnoreCase(answer.getQuestion().getCorrectAnswer())) {
                correctAnswers++;
            }
        }

        int totalQuestions = exam.getQuestions().size();
        double score = totalQuestions > 0 ? (correctAnswers * 100.0) / totalQuestions : 0;

        return SubmitExamResponseDto.builder()
                .attendanceId(attendanceId)
                .submittedAt(attendance.getSubmittedAt())
                .correctAnswers(correctAnswers)
                .totalQuestions(totalQuestions)
                .score(score)
                .build();
    }

    public List<StudentExamHistoryDto> getStudentExamHistory() {
        return attendanceRepository.findCompletedByStudentId(SecurityHelper.getCurrentUser().getId()).stream()
                .map(attendance -> {
                    double score = calculateScore(attendance.getId());
                    return StudentExamHistoryDto.builder()
                            .attendanceId(attendance.getId())
                            .examId(attendance.getExam().getId())
                            .examTopic(attendance.getExam().getTopic())
                            .courseName(attendance.getExam().getCourse().getName())
                            .startedAt(attendance.getStartedAt())
                            .submittedAt(attendance.getSubmittedAt())
                            .score(score)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<InProgressExamDto> getInProgressExams() {
        return attendanceRepository.findInProgressByStudentId(SecurityHelper.getCurrentUser().getId()).stream()
                .map(attendance -> InProgressExamDto.builder()
                        .attendanceId(attendance.getId())
                        .examId(attendance.getExam().getId())
                        .examTopic(attendance.getExam().getTopic())
                        .courseName(attendance.getExam().getCourse().getName())
                        .startedAt(attendance.getStartedAt())
                        .duration(attendance.getExam().getDuration())
                        .deadline(attendance.getExam().getDeadline())
                        .build())
                .collect(Collectors.toList());
    }

    public ExamResultDto getExamResult(Long attendanceId) {
        ExamAttendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        if (!attendance.getStudent().getId().equals(SecurityHelper.getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (attendance.getSubmittedAt() == null) {
            throw new RuntimeException("Exam not yet submitted");
        }

        List<ExamAnswer> answers = answerRepository.findByAttendanceIdWithQuestion(attendanceId);

        List<QuestionResultDto> questionResults = answers.stream()
                .map(answer -> QuestionResultDto.builder()
                        .questionId(answer.getQuestion().getId())
                        .question(answer.getQuestion().getQuestion())
                        .selectedAnswer(answer.getSelectedAnswer())
                        .correctAnswer(answer.getQuestion().getCorrectAnswer())
                        .isCorrect(answer.getSelectedAnswer().equalsIgnoreCase(answer.getQuestion().getCorrectAnswer()))
                        .build())
                .collect(Collectors.toList());

        int correctAnswers = (int) questionResults.stream().filter(QuestionResultDto::isCorrect).count();
        int totalQuestions = attendance.getExam().getQuestions().size();
        double score = totalQuestions > 0 ? (correctAnswers * 100.0) / totalQuestions : 0;

        return ExamResultDto.builder()
                .attendanceId(attendanceId)
                .examTopic(attendance.getExam().getTopic())
                .startedAt(attendance.getStartedAt())
                .submittedAt(attendance.getSubmittedAt())
                .correctAnswers(correctAnswers)
                .totalQuestions(totalQuestions)
                .score(score)
                .questionResults(questionResults)
                .build();
    }

    // ==================== COMMON METHODS ====================

    public List<ExamResponseDto> getActiveExamsForCourse() {
        UUID studentId = SecurityHelper.getCurrentUser().getId();
        List<Long> courseIds = studentRoleRepository.getCourseIdsOfStudent(studentId);
        return examRepository.findByCourseIdInAndStatus(courseIds, ExamStatus.ACTIVE).stream()
                .map(exam -> {
                    ExamResponseDto dto = mapToResponseDto(exam);
                    boolean isSubmitted = attendanceRepository.findByExamIdAndStudentId(exam.getId(), studentId)
                            .map(attendance -> attendance.getSubmittedAt() != null)
                            .orElse(false);
                    dto.setIsSubmitted(isSubmitted);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ExamResponseDto getExamById(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        return mapToResponseDto(exam);
    }

    // ==================== HELPER METHODS ====================

    private Exam getExamByIdAndTeacher(Long examId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (!exam.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Only the exam creator can perform this action");
        }

        return exam;
    }

    private Question createQuestion(QuestionRequestDto dto, Exam exam) {
        Question question = new Question();
        question.setQuestion(dto.getQuestion());
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setExam(exam);
        return question;
    }

    private double calculateScore(Long attendanceId) {
        List<ExamAnswer> answers = answerRepository.findByAttendanceIdWithQuestion(attendanceId);
        if (answers.isEmpty()) return 0;

        int correctAnswers = 0;
        for (ExamAnswer answer : answers) {
            if (answer.getSelectedAnswer().equalsIgnoreCase(answer.getQuestion().getCorrectAnswer())) {
                correctAnswers++;
            }
        }

        return (correctAnswers * 100.0) / answers.size();
    }

    private ExamResponseDto mapToResponseDto(Exam exam) {
        return ExamResponseDto.builder()
                .id(exam.getId())
                .topic(exam.getTopic())
                .description(exam.getDescription())
                .status(exam.getStatus())
                .difficulty(exam.getDifficulty())
                .areas(exam.getAreas() != null ? List.of(exam.getAreas().split(",")) : List.of())
                .duration(exam.getDuration())
                .deadline(exam.getDeadline())
                .courseId(exam.getCourse().getId())
                .courseName(exam.getCourse().getName())
                .teacherId(exam.getTeacher().getId())
                .teacherName(exam.getTeacher().getFirstName() + " " + exam.getTeacher().getLastName())
                .createdAt(exam.getCreatedAt())
                .questionCount(exam.getQuestions().size())
                .submittedCount(attendanceRepository.countSubmittedByExamId(exam.getId()))
                .build();
    }

    private AttendanceResponseDto mapToAttendanceDto(ExamAttendance attendance) {
        return AttendanceResponseDto.builder()
                .id(attendance.getId())
                .examId(attendance.getExam().getId())
                .studentId(attendance.getStudent().getId())
                .startedAt(attendance.getStartedAt())
                .build();
    }
}