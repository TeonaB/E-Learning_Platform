package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Review;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.ReviewDto;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.mapper.ReviewMapper;
import com.elearning.platform.service.interf.CourseService;
import com.elearning.platform.service.interf.ReviewService;
import com.elearning.platform.service.interf.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class ReviewWebController {

    private final ReviewService reviewService;
    private final CourseService courseService;
    private final UserService userService;
    private final ReviewMapper reviewMapper;

    // USER: Show form to leave a review
    @GetMapping("/reviews/create/{courseId}")
    public String showReviewForm(@PathVariable Long courseId, Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        // Verify enrollment
        User freshUser = userService.getUserById(user.getId());
        boolean isEnrolled = freshUser.getCourses().stream().anyMatch(c -> c.getId().equals(courseId));
        if (!isEnrolled) {
            return "redirect:/web/home"; // Not enrolled, block
        }

        Course course = courseService.getCourseById(courseId);
        model.addAttribute("review", new ReviewDto());
        model.addAttribute("course", course);
        return "review/form";
    }

    // USER/ADMIN: Save/Update review
    @PostMapping("/reviews/save/{courseId}")
    public String saveReview(@Valid @ModelAttribute("review") ReviewDto reviewDto,
                             BindingResult bindingResult,
                             @PathVariable Long courseId,
                             @RequestParam(required = false) Long reviewId,
                             Model model,
                             Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("course", courseService.getCourseById(courseId));
            if (reviewId != null) {
                model.addAttribute("reviewId", reviewId);
            }
            return "review/form";
        }

        Review review = reviewMapper.toReview(reviewDto);

        try {
            if (reviewId != null) {
                Review existingReview = reviewService.getReviewById(reviewId);
                // Security check
                if (!user.getRole().name().equals("ADMIN") && !existingReview.getUser().getId().equals(user.getId())) {
                    return "redirect:/web/auth/login";
                }
                reviewService.updateReview(reviewId, review);
            } else {
                reviewService.createReview(user.getId(), courseId, review);
            }
            return "redirect:/web/courses/" + courseId + "/lessons";
        } catch (BadRequestException ex) {
            model.addAttribute("saveError", ex.getMessage());
            model.addAttribute("course", courseService.getCourseById(courseId));
            if (reviewId != null) {
                model.addAttribute("reviewId", reviewId);
            }
            return "review/form";
        }
    }

    // USER/ADMIN: Show form to edit a review
    @GetMapping("/reviews/edit/{id}")
    public String showEditReviewForm(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        Review review = reviewService.getReviewById(id);
        // Security check: Only author or ADMIN
        if (!user.getRole().name().equals("ADMIN") && !review.getUser().getId().equals(user.getId())) {
            return "redirect:/web/home";
        }

        ReviewDto reviewDto = reviewMapper.toReviewDto(review);

        model.addAttribute("reviewId", id);
        model.addAttribute("review", reviewDto);
        model.addAttribute("course", review.getCourse());
        return "review/form";
    }

    // USER: Delete own review
    @GetMapping("/reviews/delete/{id}")
    public String deleteReviewForStudent(@PathVariable Long id, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        Review review = reviewService.getReviewById(id);
        // Security check: Only author or ADMIN
        if (!user.getRole().name().equals("ADMIN") && !review.getUser().getId().equals(user.getId())) {
            return "redirect:/web/home";
        }

        Long courseId = review.getCourse().getId();
        reviewService.deleteReview(id);
        return "redirect:/web/courses/" + courseId + "/lessons";
    }

    // ADMIN: View all reviews list
    @GetMapping("/admin/reviews")
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "review/list";
    }

    // ADMIN: Delete review
    @GetMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/web/admin/reviews";
    }
}
