package com.elearning.platform.service.interf;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);

    List<Course> getCoursesByCategory(Long categoryId);
}