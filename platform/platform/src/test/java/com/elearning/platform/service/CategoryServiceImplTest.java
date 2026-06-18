package com.elearning.platform.service;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.CategoryRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void testGetAllCategories() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Development");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Business");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2));

        List<Category> categories = categoryService.getAllCategories();
        assertEquals(2, categories.size());
        assertEquals("Development", categories.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoryById_Success() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Development");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

        Category found = categoryService.getCategoryById(1L);
        assertNotNull(found);
        assertEquals("Development", found.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateCategory_Success() {
        Category cat = new Category();
        cat.setName("New Cat");

        when(categoryRepository.existsByName("New Cat")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category created = categoryService.createCategory(cat);
        assertNotNull(created);
        assertEquals("New Cat", created.getName());
        verify(categoryRepository, times(1)).existsByName("New Cat");
        verify(categoryRepository, times(1)).save(cat);
    }

    @Test
    void testCreateCategory_AlreadyExists() {
        Category cat = new Category();
        cat.setName("Existing");

        when(categoryRepository.existsByName("Existing")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.createCategory(cat));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_Success() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setDescription("Old Desc");

        Category updatedInfo = new Category();
        updatedInfo.setName("New Name");
        updatedInfo.setDescription("New Desc");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("New Name")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.updateCategory(1L, updatedInfo);
        assertEquals("New Name", result.getName());
        assertEquals("New Desc", result.getDescription());
        verify(categoryRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateCategory_DuplicateName() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Old Name");

        Category updatedInfo = new Category();
        updatedInfo.setName("Duplicate Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("Duplicate Name")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.updateCategory(1L, updatedInfo));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDeleteCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetCoursesByCategory() {
        Category cat = new Category();
        cat.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

        Course course = new Course();
        course.setId(10L);
        course.setTitle("Java Programming");
        when(courseRepository.findByCategoryId(1L)).thenReturn(Collections.singletonList(course));

        List<Course> courses = categoryService.getCoursesByCategory(1L);
        assertEquals(1, courses.size());
        assertEquals("Java Programming", courses.get(0).getTitle());
    }
}
