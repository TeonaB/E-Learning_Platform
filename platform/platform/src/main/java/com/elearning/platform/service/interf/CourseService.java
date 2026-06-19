package com.elearning.platform.service.interf;

import com.elearning.platform.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseService {

    List<Course> getAllCourses();

    Course getCourseById(Long id);

    Course createCourse(Course course);

    Course updateCourse(Long id, Course course);

    void deleteCourse(Long id);

    List<Course> getCoursesByCategory(Long categoryId);

    void enrollUser(Long courseId, Long userId);

    Page<Course> getCoursesPaged(Long categoryId, Pageable pageable, String sortBy, String sortDir);
}