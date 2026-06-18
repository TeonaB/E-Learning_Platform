package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Review;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.ReviewDto;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.service.interf.CourseService;
import com.elearning.platform.service.interf.ReviewService;
import com.elearning.platform.service.interf.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class ReviewWebController {

    private final ReviewService reviewService;
    private final CourseService courseService;
    private final UserService userService;

    // USER: Show form to leave a review
    @GetMapping("/reviews/create/{courseId}")
    public String showReviewForm(@PathVariable Long courseId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/web/auth/login";
        }

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

    // USER: Save review
    @PostMapping("/reviews/save/{courseId}")
    public String saveReview(@Valid @ModelAttribute("review") ReviewDto reviewDto,
                             BindingResult bindingResult,
                             @PathVariable Long courseId,
                             Model model,
                             HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/web/auth/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("course", courseService.getCourseById(courseId));
            return "review/form";
        }

        Review review = new Review();
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());

        try {
            reviewService.createReview(user.getId(), courseId, review);
            return "redirect:/web/courses/" + courseId + "/lessons";
        } catch (BadRequestException ex) {
            model.addAttribute("saveError", ex.getMessage());
            model.addAttribute("course", courseService.getCourseById(courseId));
            return "review/form";
        }
    }

    // ADMIN: View all reviews list
    @GetMapping("/admin/reviews")
    public String listReviews(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "review/list";
    }

    // ADMIN: Delete review
    @GetMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        reviewService.deleteReview(id);
        return "redirect:/web/admin/reviews";
    }
}
