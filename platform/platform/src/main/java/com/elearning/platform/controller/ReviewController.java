package com.elearning.platform.controller;

import com.elearning.platform.domain.Review;
import com.elearning.platform.dto.ReviewDto;
import com.elearning.platform.dto.ReviewResponseDto;
import com.elearning.platform.mapper.ResponseMapper;
import com.elearning.platform.service.interf.ReviewService;
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
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews().stream().map(ResponseMapper::toReviewDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ResponseMapper.toReviewDto(reviewService.getReviewById(id)));
    }

    @PostMapping("/{courseId}/{userId}")
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable @Positive Long courseId,
                                                          @PathVariable @Positive Long userId,
                                                          @Valid @RequestBody ReviewDto reviewDto) {
        Review review = new Review();
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());

        Review saved = reviewService.createReview(userId, courseId, review);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(ResponseMapper.toReviewDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable @Positive Long id,
                                                          @Valid @RequestBody ReviewDto reviewDto) {
        Review review = new Review();
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());

        Review updated = reviewService.updateReview(id, review);
        return ResponseEntity.ok(ResponseMapper.toReviewDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable @Positive Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ReviewResponseDto>> getByCourse(@PathVariable @Positive Long courseId) {
        return ResponseEntity.ok(reviewService.getReviewsByCourse(courseId).stream().map(ResponseMapper::toReviewDto).toList());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getByUser(@PathVariable @Positive Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId).stream().map(ResponseMapper::toReviewDto).toList());
    }

    @GetMapping("/course/{courseId}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable @Positive Long courseId) {
        Double avg = reviewService.getAverageRating(courseId);
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }
}