package com.elearning.platform.mapper;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Lesson;
import com.elearning.platform.domain.Review;
import com.elearning.platform.domain.User;
import com.elearning.platform.domain.UserProfile;
import com.elearning.platform.dto.CategoryResponseDto;
import com.elearning.platform.dto.CourseResponseDto;
import com.elearning.platform.dto.LessonResponseDto;
import com.elearning.platform.dto.ReviewResponseDto;
import com.elearning.platform.dto.UserProfileResponseDto;
import com.elearning.platform.dto.UserResponseDto;

import java.util.List;
import java.util.Objects;

public final class ResponseMapper {

    private ResponseMapper() {
    }

    public static CategoryResponseDto toCategoryDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    public static LessonResponseDto toLessonDto(Lesson lesson) {
        LessonResponseDto dto = new LessonResponseDto();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setContentUrl(lesson.getContentUrl());
        dto.setDurationMinutes(lesson.getDurationMinutes());
        dto.setCourseId(lesson.getCourse() != null ? lesson.getCourse().getId() : null);
        return dto;
    }

    public static ReviewResponseDto toReviewDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUserId(review.getUser() != null ? review.getUser().getId() : null);
        dto.setUsername(review.getUser() != null ? review.getUser().getUsername() : null);
        dto.setCourseId(review.getCourse() != null ? review.getCourse().getId() : null);
        return dto;
    }

    public static CourseResponseDto toCourseDto(Course course) {
        CourseResponseDto dto = new CourseResponseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        if (course.getCategory() != null) {
            dto.setCategoryId(course.getCategory().getId());
            dto.setCategoryName(course.getCategory().getName());
        }

        List<ReviewResponseDto> reviewDtos = course.getReviews() == null
                ? List.of()
                : course.getReviews().stream().map(ResponseMapper::toReviewDto).toList();
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

    public static UserResponseDto toUserDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        return dto;
    }

    public static UserProfileResponseDto toUserProfileDto(UserProfile profile) {
        UserProfileResponseDto dto = new UserProfileResponseDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser() != null ? profile.getUser().getId() : null);
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setPhoneNumber(profile.getPhoneNumber());
        return dto;
    }
}

