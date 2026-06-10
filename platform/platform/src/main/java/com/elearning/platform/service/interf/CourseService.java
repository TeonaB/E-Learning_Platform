package com.elearning.platform.service.interf;

import com.elearning.platform.domain.Course;
import java.util.List;

public interface CourseService {

    List<Course> getAllCourses();

    Course getCourseById(Long id);

    Course createCourse(Course course);

    Course updateCourse(Long id, Course course);

    void deleteCourse(Long id);

    List<Course> getCoursesByCategory(Long categoryId);

    void enrollUser(Long courseId, Long userId);
}