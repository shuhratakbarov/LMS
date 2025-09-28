package uz.shuhrat.lms.projection;

public interface TeacherGroupSummaryProjection {
    Long getId();

    String getGroupName();

    String getCourseName();

    Long getCourseId();

    Long getStudentCount();

    Long getTaskCount();

}
