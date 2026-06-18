package com.elearning.platform.service;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Lesson;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.LessonRepository;
import com.elearning.platform.service.impl.LessonServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private LessonServiceImpl lessonService;

    @Test
    void testGetAllLessons() {
        Lesson lesson = new Lesson();
        lesson.setId(1L);
        lesson.setTitle("Java Intro");

        when(lessonRepository.findAll()).thenReturn(Collections.singletonList(lesson));

        List<Lesson> lessons = lessonService.getAllLessons();
        assertEquals(1, lessons.size());
        assertEquals("Java Intro", lessons.get(0).getTitle());
    }

    @Test
    void testGetLessonById_Success() {
        Lesson lesson = new Lesson();
        lesson.setId(1L);
        lesson.setTitle("Java Intro");

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        Lesson found = lessonService.getLessonById(1L);
        assertNotNull(found);
        assertEquals("Java Intro", found.getTitle());
    }

    @Test
    void testGetLessonById_NotFound() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonService.getLessonById(1L));
    }

    @Test
    void testCreateLesson_Success() {
        Course course = new Course();
        course.setId(2L);

        Lesson lesson = new Lesson();
        lesson.setTitle("Variables");

        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Lesson created = lessonService.createLesson(2L, lesson);
        assertNotNull(created);
        assertEquals("Variables", created.getTitle());
        assertEquals(course, created.getCourse());
        verify(lessonRepository, times(1)).save(lesson);
    }

    @Test
    void testCreateLesson_CourseNotFound() {
        Lesson lesson = new Lesson();
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonService.createLesson(2L, lesson));
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void testUpdateLesson_Success() {
        Lesson existing = new Lesson();
        existing.setId(1L);
        existing.setTitle("Old Title");
        existing.setContentUrl("Old Url");
        existing.setDurationMinutes(10);

        Lesson updatedInfo = new Lesson();
        updatedInfo.setTitle("New Title");
        updatedInfo.setContentUrl("New Url");
        updatedInfo.setDurationMinutes(20);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Lesson result = lessonService.updateLesson(1L, updatedInfo);
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Url", result.getContentUrl());
        assertEquals(20, result.getDurationMinutes());
    }

    @Test
    void testDeleteLesson_Success() {
        when(lessonRepository.existsById(1L)).thenReturn(true);
        doNothing().when(lessonRepository).deleteById(1L);

        lessonService.deleteLesson(1L);
        verify(lessonRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteLesson_NotFound() {
        when(lessonRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> lessonService.deleteLesson(1L));
    }

    @Test
    void testGetLessonsByCourse_Success() {
        Course course = new Course();
        course.setId(2L);

        Lesson lesson = new Lesson();
        lesson.setId(1L);
        lesson.setTitle("Java Basics");

        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(lessonRepository.findByCourseId(2L)).thenReturn(Collections.singletonList(lesson));

        List<Lesson> result = lessonService.getLessonsByCourse(2L);
        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
    }

    @Test
    void testGetLessonsByCourse_CourseNotFound() {
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonService.getLessonsByCourse(2L));
    }
}
