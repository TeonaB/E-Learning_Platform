package com.elearning.platform.service;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.User;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.CategoryRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.UserRepository;
import com.elearning.platform.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void testGetAllCourses() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Spring Boot");

        when(courseRepository.findAll()).thenReturn(Collections.singletonList(course));

        List<Course> list = courseService.getAllCourses();
        assertEquals(1, list.size());
        assertEquals("Spring Boot", list.get(0).getTitle());
    }

    @Test
    void testGetCourseById_Success() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Spring Boot");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course found = courseService.getCourseById(1L);
        assertNotNull(found);
        assertEquals("Spring Boot", found.getTitle());
    }

    @Test
    void testGetCourseById_NotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(1L));
    }

    @Test
    void testCreateCourse_Success() {
        Category category = new Category();
        category.setId(2L);

        Course course = new Course();
        course.setTitle("New Course");
        course.setCategory(category);

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Course created = courseService.createCourse(course);
        assertNotNull(created);
        assertEquals("New Course", created.getTitle());
        assertEquals(category, created.getCategory());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testCreateCourse_CategoryNotFound() {
        Category category = new Category();
        category.setId(2L);

        Course course = new Course();
        course.setCategory(category);

        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.createCourse(course));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testUpdateCourse_Success() {
        Course existing = new Course();
        existing.setId(1L);
        existing.setTitle("Old Title");

        Category category = new Category();
        category.setId(2L);

        Course updatedInfo = new Course();
        updatedInfo.setTitle("New Title");
        updatedInfo.setDescription("New Desc");
        updatedInfo.setCategory(category);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Course result = courseService.updateCourse(1L, updatedInfo);
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Desc", result.getDescription());
        assertEquals(category, result.getCategory());
    }

    @Test
    void testUpdateCourse_CategoryNotFound() {
        Course existing = new Course();
        existing.setId(1L);

        Category category = new Category();
        category.setId(2L);

        Course updatedInfo = new Course();
        updatedInfo.setCategory(category);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(1L, updatedInfo));
    }

    @Test
    void testDeleteCourse_Success() {
        when(courseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteCourse(1L);
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCourse_NotFound() {
        when(courseRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> courseService.deleteCourse(1L));
    }

    @Test
    void testGetCoursesByCategory() {
        when(courseRepository.findByCategoryId(1L)).thenReturn(Collections.emptyList());

        List<Course> list = courseService.getCoursesByCategory(1L);
        assertTrue(list.isEmpty());
    }

    @Test
    void testEnrollUser_Success() {
        Course course = new Course();
        course.setId(1L);
        course.setUsers(new ArrayList<>());

        User user = new User();
        user.setId(2L);
        user.setCourses(new ArrayList<>());

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        courseService.enrollUser(1L, 2L);

        assertTrue(course.getUsers().contains(user));
        assertTrue(user.getCourses().contains(course));
        verify(courseRepository, times(1)).save(course);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testEnrollUser_AlreadyEnrolled() {
        User user = new User();
        user.setId(2L);
        user.setCourses(new ArrayList<>());

        Course course = new Course();
        course.setId(1L);
        course.setUsers(new ArrayList<>(Collections.singletonList(user)));
        user.getCourses().add(course);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        courseService.enrollUser(1L, 2L);

        verify(courseRepository, never()).save(any(Course.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
