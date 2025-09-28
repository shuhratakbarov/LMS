package uz.shuhrat.lms.db.customDto.teacher;

public interface TeacherGroupSummaryProjection {
    Long getId();

    String getGroupName();

    String getCourseName();

    Long getCourseId();

    Long getStudentCount();

    Long getTaskCount();

}
