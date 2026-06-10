package com.elearning.platform.service.interf;

import com.elearning.platform.domain.Review;

import java.util.List;

public interface ReviewService {

    List<Review> getAllReviews();

    Review getReviewById(Long id);

    Review createReview(Long userId, Long courseId, Review review);

    Review updateReview(Long id, Review review);

    void deleteReview(Long id);

    List<Review> getReviewsByCourse(Long courseId);

    List<Review> getReviewsByUser(Long userId);

    Double getAverageRating(Long courseId);
}