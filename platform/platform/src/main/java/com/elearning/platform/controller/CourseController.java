package com.elearning.platform.controller;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.dto.CourseDto;
import com.elearning.platform.dto.CourseResponseDto;
import com.elearning.platform.mapper.ResponseMapper;
import com.elearning.platform.service.interf.CourseService;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
@Validated
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses().stream().map(ResponseMapper::toCourseDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ResponseMapper.toCourseDto(courseService.getCourseById(id)));
    }

    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(@Valid @RequestBody CourseDto courseDto) {
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        Category category = new Category();
        category.setId(courseDto.getCategoryId());
        course.setCategory(category);

        Course saved = courseService.createCourse(course);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(ResponseMapper.toCourseDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(@PathVariable @Positive Long id,
                                                          @Valid @RequestBody CourseDto courseDto) {
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        Category category = new Category();
        category.setId(courseDto.getCategoryId());
        course.setCategory(category);

        Course updated = courseService.updateCourse(id, course);
        return ResponseEntity.ok(ResponseMapper.toCourseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable @Positive Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseResponseDto>> getByCategory(@PathVariable @Positive Long categoryId) {
        return ResponseEntity.ok(courseService.getCoursesByCategory(categoryId).stream().map(ResponseMapper::toCourseDto).toList());
    }

    @PostMapping("/{courseId}/enroll/{userId}")
    public ResponseEntity<Void> enrollUser(@PathVariable @Positive Long courseId,
                                           @PathVariable @Positive Long userId) {
        courseService.enrollUser(courseId, userId);
        return ResponseEntity.noContent().build();
    }
}