package com.elearning.platform.service;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Review;
import com.elearning.platform.domain.User;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.ReviewRepository;
import com.elearning.platform.repository.UserRepository;
import com.elearning.platform.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void testGetAllReviews() {
        Review review = new Review();
        review.setId(1L);
        when(reviewRepository.findAll()).thenReturn(Collections.singletonList(review));

        List<Review> list = reviewService.getAllReviews();
        assertEquals(1, list.size());
    }

    @Test
    void testGetReviewById_Success() {
        Review review = new Review();
        review.setId(1L);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        Review found = reviewService.getReviewById(1L);
        assertNotNull(found);
    }

    @Test
    void testGetReviewById_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.getReviewById(1L));
    }

    @Test
    void testCreateReview_Success() {
        User user = new User();
        user.setId(10L);

        Course course = new Course();
        course.setId(20L);
        course.setUsers(new ArrayList<>(Collections.singletonList(user)));

        Review review = new Review();
        review.setRating(5);
        review.setComment("Awesome!");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(20L)).thenReturn(Optional.of(course));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review created = reviewService.createReview(10L, 20L, review);
        assertNotNull(created);
        assertEquals(5, created.getRating());
        assertEquals("Awesome!", created.getComment());
        assertEquals(user, created.getUser());
        assertEquals(course, created.getCourse());
    }

    @Test
    void testCreateReview_UserNotFound() {
        Review review = new Review();
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.createReview(10L, 20L, review));
    }

    @Test
    void testCreateReview_CourseNotFound() {
        User user = new User();
        user.setId(10L);
        Review review = new Review();

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.createReview(10L, 20L, review));
    }

    @Test
    void testCreateReview_UserNotEnrolled() {
        User user = new User();
        user.setId(10L);

        Course course = new Course();
        course.setId(20L);
        course.setUsers(new ArrayList<>()); // empty users

        Review review = new Review();
        review.setRating(5);

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(20L)).thenReturn(Optional.of(course));

        assertThrows(BadRequestException.class, () -> reviewService.createReview(10L, 20L, review));
    }

    @Test
    void testCreateReview_InvalidRatingLow() {
        User user = new User();
        user.setId(10L);

        Course course = new Course();
        course.setId(20L);
        course.setUsers(new ArrayList<>(Collections.singletonList(user)));

        Review review = new Review();
        review.setRating(0); // too low

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(20L)).thenReturn(Optional.of(course));

        assertThrows(BadRequestException.class, () -> reviewService.createReview(10L, 20L, review));
    }

    @Test
    void testCreateReview_InvalidRatingHigh() {
        User user = new User();
        user.setId(10L);

        Course course = new Course();
        course.setId(20L);
        course.setUsers(new ArrayList<>(Collections.singletonList(user)));

        Review review = new Review();
        review.setRating(6); // too high

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(20L)).thenReturn(Optional.of(course));

        assertThrows(BadRequestException.class, () -> reviewService.createReview(10L, 20L, review));
    }

    @Test
    void testUpdateReview_Success() {
        Review existing = new Review();
        existing.setId(1L);
        existing.setRating(4);
        existing.setComment("Good");

        Review updated = new Review();
        updated.setRating(5);
        updated.setComment("Excellent");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.updateReview(1L, updated);
        assertEquals(5, result.getRating());
        assertEquals("Excellent", result.getComment());
    }

    @Test
    void testUpdateReview_InvalidRating() {
        Review existing = new Review();
        existing.setId(1L);

        Review updated = new Review();
        updated.setRating(10); // invalid

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> reviewService.updateReview(1L, updated));
    }

    @Test
    void testDeleteReview_Success() {
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.deleteReview(1L);
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteReview_NotFound() {
        when(reviewRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview(1L));
    }

    @Test
    void testGetReviewsByCourse() {
        when(reviewRepository.findByCourseId(1L)).thenReturn(Collections.emptyList());
        List<Review> list = reviewService.getReviewsByCourse(1L);
        assertTrue(list.isEmpty());
    }

    @Test
    void testGetReviewsByUser() {
        when(reviewRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        List<Review> list = reviewService.getReviewsByUser(1L);
        assertTrue(list.isEmpty());
    }

    @Test
    void testGetAverageRating() {
        when(reviewRepository.getAverageRating(1L)).thenReturn(4.5);
        Double rating = reviewService.getAverageRating(1L);
        assertEquals(4.5, rating);
    }
}
