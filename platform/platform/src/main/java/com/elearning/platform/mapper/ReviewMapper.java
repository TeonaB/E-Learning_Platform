package com.elearning.platform.mapper;

import com.elearning.platform.domain.Review;
import com.elearning.platform.dto.ReviewDto;
import com.elearning.platform.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

    private final ModelMapper modelMapper;

    public ReviewResponseDto toReviewResponseDto(Review review) {
        if (review == null) {
            return null;
        }
        ReviewResponseDto dto = modelMapper.map(review, ReviewResponseDto.class);
        dto.setUserId(review.getUser() != null ? review.getUser().getId() : null);
        dto.setUsername(review.getUser() != null ? review.getUser().getUsername() : null);
        dto.setCourseId(review.getCourse() != null ? review.getCourse().getId() : null);
        return dto;
    }

    public ReviewDto toReviewDto(Review review) {
        if (review == null) {
            return null;
        }
        return modelMapper.map(review, ReviewDto.class);
    }

    public Review toReview(ReviewDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Review.class);
    }
}
