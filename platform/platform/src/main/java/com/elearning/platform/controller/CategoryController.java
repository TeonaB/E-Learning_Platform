package com.elearning.platform.controller;

import com.elearning.platform.domain.Category;
import com.elearning.platform.dto.CategoryDto;
import com.elearning.platform.dto.CategoryResponseDto;
import com.elearning.platform.dto.CourseResponseDto;
import com.elearning.platform.mapper.ResponseMapper;
import com.elearning.platform.service.interf.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories().stream().map(ResponseMapper::toCategoryDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getById(@PathVariable @Positive Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ResponseMapper.toCategoryDto(category));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category saved = categoryService.createCategory(category);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(ResponseMapper.toCategoryDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable @Positive Long id,
                                                      @Valid @RequestBody CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(ResponseMapper.toCategoryDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseResponseDto>> getCourses(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(categoryService.getCoursesByCategory(id).stream().map(ResponseMapper::toCourseDto).toList());
    }
}