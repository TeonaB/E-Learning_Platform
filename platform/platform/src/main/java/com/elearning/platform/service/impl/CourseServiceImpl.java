package com.elearning.platform.service.impl;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.User;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.CategoryRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.UserRepository;
import com.elearning.platform.service.interf.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + id));
    }

    @Override
    @Transactional
    public Course createCourse(Course course) {
        Long categoryId = course.getCategory() != null ? course.getCategory().getId() : null;
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));
        course.setCategory(category);
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public Course updateCourse(Long id, Course updated) {
        Course course = getCourseById(id);

        Long categoryId = updated.getCategory() != null ? updated.getCategory().getId() : null;
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));

        course.setTitle(updated.getTitle());
        course.setDescription(updated.getDescription());
        course.setCategory(category);

        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id " + id);
        }
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> getCoursesByCategory(Long categoryId) {
        return courseRepository.findByCategoryId(categoryId);
    }


    @Override
    @Transactional
    public void enrollUser(Long courseId, Long userId) {
        Course course = getCourseById(courseId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        if (!course.getUsers().contains(user)) {
            course.getUsers().add(user);
            user.getCourses().add(course);

            courseRepository.save(course);
            userRepository.save(user);
        }
    }
}