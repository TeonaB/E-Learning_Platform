package com.elearning.platform.service.impl;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Review;
import com.elearning.platform.domain.User;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.ReviewRepository;
import com.elearning.platform.repository.UserRepository;
import com.elearning.platform.service.interf.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + id));
    }

    @Override
    @Transactional
    public Review createReview(Long userId, Long courseId, Review review) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + courseId));

        if (course.getUsers().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new BadRequestException("User must be enrolled in this course before leaving a review");
        }

        if (review.getRating() != null && (review.getRating() < 1 || review.getRating() > 5)) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        review.setUser(user);
        review.setCourse(course);

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review updateReview(Long id, Review updated) {
        Review review = getReviewById(id);

        if (updated.getRating() != null && (updated.getRating() < 1 || updated.getRating() > 5)) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        review.setComment(updated.getComment());
        review.setRating(updated.getRating());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> getReviewsByCourse(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }

    @Override
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public Double getAverageRating(Long courseId) {
        return reviewRepository.getAverageRating(courseId);
    }
}