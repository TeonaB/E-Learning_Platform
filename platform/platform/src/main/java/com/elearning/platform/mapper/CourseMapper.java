package com.elearning.platform.mapper;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.dto.CourseDto;
import com.elearning.platform.dto.CourseResponseDto;
import com.elearning.platform.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final ModelMapper modelMapper;
    private final ReviewMapper reviewMapper;

    public CourseResponseDto toCourseResponseDto(Course course) {
        if (course == null) {
            return null;
        }
        CourseResponseDto dto = modelMapper.map(course, CourseResponseDto.class);
        if (course.getCategory() != null) {
            dto.setCategoryId(course.getCategory().getId());
            dto.setCategoryName(course.getCategory().getName());
        }

        List<ReviewResponseDto> reviewDtos = course.getReviews() == null
                ? List.of()
                : course.getReviews().stream().map(reviewMapper::toReviewResponseDto).toList();
        dto.setReviews(reviewDtos);

        double avg = reviewDtos.stream()
                .map(ReviewResponseDto::getRating)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        dto.setAverageRating(avg);

        return dto;
    }

    public CourseDto toCourseDto(Course course) {
        if (course == null) {
            return null;
        }
        CourseDto dto = modelMapper.map(course, CourseDto.class);
        if (course.getCategory() != null) {
            dto.setCategoryId(course.getCategory().getId());
        }
        return dto;
    }

    public Course toCourse(CourseDto dto) {
        if (dto == null) {
            return null;
        }
        Course course = modelMapper.map(dto, Course.class);
        course.setId(null);
        if (dto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            course.setCategory(category);
        }
        return course;
    }
}
