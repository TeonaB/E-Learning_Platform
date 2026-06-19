package com.elearning.platform.service.impl;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.CategoryRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.service.interf.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if (category.getName() != null && categoryRepository.existsByName(category.getName())) {
            throw new BadRequestException("Category with name '" + category.getName() + "' already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category updated) {
        Category category = getCategoryById(id);

        if (updated.getName() != null && !updated.getName().equals(category.getName())
                && categoryRepository.existsByName(updated.getName())) {
            throw new BadRequestException("Category with name '" + updated.getName() + "' already exists");
        }

        category.setName(updated.getName());
        category.setDescription(updated.getDescription());

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<Course> getCoursesByCategory(Long categoryId) {
        // ensure category exists
        getCategoryById(categoryId);
        return courseRepository.findByCategoryId(categoryId);
    }

    @Override
    public Page<Category> getCategoriesPaged(Pageable pageable, String sortBy, String sortDir) {
        if ("coursesCount".equals(sortBy)) {
            return "desc".equalsIgnoreCase(sortDir)
                    ? categoryRepository.findAllSortedByCoursesCountDesc(pageable)
                    : categoryRepository.findAllSortedByCoursesCountAsc(pageable);
        }
        return categoryRepository.findAll(pageable);
    }
}