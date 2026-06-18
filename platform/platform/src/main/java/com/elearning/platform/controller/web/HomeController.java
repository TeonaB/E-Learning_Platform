package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.CategoryResponseDto;
import com.elearning.platform.dto.CourseResponseDto;
import com.elearning.platform.mapper.ResponseMapper;
import com.elearning.platform.service.interf.CategoryService;
import com.elearning.platform.service.interf.CourseService;
import com.elearning.platform.service.interf.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class HomeController {

    private final CourseService courseService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("/home")
    public String home(@RequestParam(required = false) Long categoryId, Model model, HttpSession session) {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponseDto> categoryDtos = categories.stream()
                .map(ResponseMapper::toCategoryDto)
                .toList();
        model.addAttribute("categories", categoryDtos);

        List<Course> courses;
        if (categoryId != null) {
            courses = courseService.getCoursesByCategory(categoryId);
            categoryService.getAllCategories().stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .ifPresent(selected -> model.addAttribute("selectedCategory", ResponseMapper.toCategoryDto(selected)));
        } else {
            courses = courseService.getAllCourses();
        }

        List<CourseResponseDto> courseDtos = courses.stream()
                .map(ResponseMapper::toCourseDto)
                .toList();
        model.addAttribute("courses", courseDtos);

        // Fetch enrolled course IDs if a user is logged in
        User currentUser = (User) session.getAttribute("currentUser");
        List<Long> enrolledCourseIds = new ArrayList<>();
        if (currentUser != null && currentUser.getRole().name().equals("USER")) {
            try {
                User freshUser = userService.getUserById(currentUser.getId());
                enrolledCourseIds = freshUser.getCourses().stream().map(Course::getId).toList();
            } catch (Exception e) {
                // ignore
            }
        }
        model.addAttribute("enrolledCourseIds", enrolledCourseIds);

        return "home";
    }
}
