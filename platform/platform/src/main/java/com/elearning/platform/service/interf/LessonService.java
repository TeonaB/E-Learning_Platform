package com.elearning.platform.service.interf;

import com.elearning.platform.domain.Lesson;

import java.util.List;

public interface LessonService {

    List<Lesson> getAllLessons();

    Lesson getLessonById(Long id);

    Lesson createLesson(Long courseId, Lesson lesson);

    Lesson updateLesson(Long id, Lesson lesson);

    void deleteLesson(Long id);

    List<Lesson> getLessonsByCourse(Long courseId);
}