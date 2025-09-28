package uz.shuhrat.lms.projection;


import java.math.BigInteger;

public interface CourseGroupCountProjection {
    BigInteger getCourseCount();
    BigInteger getGroupCount();
}

