package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.CategoryResponseDto;
import com.elearning.platform.dto.CourseResponseDto;
import com.elearning.platform.mapper.CategoryMapper;
import com.elearning.platform.mapper.CourseMapper;
import com.elearning.platform.service.interf.CategoryService;
import com.elearning.platform.service.interf.CourseService;
import com.elearning.platform.service.interf.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final CategoryMapper categoryMapper;
    private final CourseMapper courseMapper;

    @GetMapping("/home")
    public String home(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model,
            HttpSession session) {

        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponseDto> categoryDtos = categories.stream()
                .map(categoryMapper::toCategoryResponseDto)
                .toList();
        model.addAttribute("categories", categoryDtos);

        if (categoryId != null) {
            categories.stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .ifPresent(selected -> model.addAttribute("selectedCategory", categoryMapper.toCategoryResponseDto(selected)));
        }

        Sort sort = "rating".equals(sortBy) 
                ? Sort.unsorted() 
                : Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> coursePage = courseService.getCoursesPaged(categoryId, pageable, sortBy, sortDir);

        List<CourseResponseDto> courseDtos = coursePage.getContent().stream()
                .map(courseMapper::toCourseResponseDto)
                .toList();

        model.addAttribute("courses", courseDtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalItems", coursePage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("categoryId", categoryId);

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
