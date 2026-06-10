package com.elearning.platform.service.impl;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Lesson;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.LessonRepository;
import com.elearning.platform.service.interf.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Override
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    @Override
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id " + id));
    }

    @Override
    @Transactional
    public Lesson createLesson(Long courseId, Lesson lesson) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + courseId));

        lesson.setCourse(course);

        return lessonRepository.save(lesson);
    }

    @Override
    @Transactional
    public Lesson updateLesson(Long id, Lesson updated) {
        Lesson lesson = getLessonById(id);

        lesson.setTitle(updated.getTitle());
        lesson.setContentUrl(updated.getContentUrl());
        lesson.setDurationMinutes(updated.getDurationMinutes());

        return lessonRepository.save(lesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lesson not found with id " + id);
        }
        lessonRepository.deleteById(id);
    }

    @Override
    public List<Lesson> getLessonsByCourse(Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + courseId));
        return lessonRepository.findByCourseId(courseId);
    }
}