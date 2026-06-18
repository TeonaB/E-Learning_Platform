package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.CourseDto;
import com.elearning.platform.service.interf.CategoryService;
import com.elearning.platform.service.interf.CourseService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.elearning.platform.service.interf.UserService;

import java.util.List;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class CourseWebController {

    private final CourseService courseService;
    private final CategoryService categoryService;
    private final UserService userService;

    // ADMIN: View courses list
    @GetMapping("/admin/courses")
    public String listCourses(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        model.addAttribute("courses", courseService.getAllCourses());
        return "course/list";
    }

    // ADMIN: Show create form
    @GetMapping("/admin/courses/create")
    public String showCreateForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        model.addAttribute("course", new CourseDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "course/form";
    }

    // ADMIN: Show edit form
    @GetMapping("/admin/courses/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        Course course = courseService.getCourseById(id);
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(course.getTitle());
        courseDto.setDescription(course.getDescription());
        courseDto.setCategoryId(course.getCategory().getId());
        
        model.addAttribute("courseId", id);
        model.addAttribute("course", courseDto);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "course/form";
    }

    // ADMIN: Save/Update course
    @PostMapping("/admin/courses/save")
    public String saveCourse(@Valid @ModelAttribute("course") CourseDto courseDto,
                             BindingResult bindingResult,
                             @RequestParam(required = false) Long courseId,
                             Model model,
                             HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            if (courseId != null) {
                model.addAttribute("courseId", courseId);
            }
            return "course/form";
        }

        Course course = new Course();
        if (courseId != null) {
            course.setId(courseId);
        }
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        Category category = new Category();
        category.setId(courseDto.getCategoryId());
        course.setCategory(category);

        if (courseId != null) {
            courseService.updateCourse(courseId, course);
        } else {
            courseService.createCourse(course);
        }

        return "redirect:/web/admin/courses";
    }

    // ADMIN: Delete course
    @GetMapping("/admin/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        courseService.deleteCourse(id);
        return "redirect:/web/admin/courses";
    }

    // USER: View enrolled courses
    @GetMapping("/courses/my-courses")
    public String myCourses(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/web/auth/login";
        }
        List<Course> myCourses = userService.getUserCourses(user.getId());
        model.addAttribute("courses", myCourses);
        return "course/my-courses";
    }

    // USER: Enroll in course
    @PostMapping("/courses/{id}/enroll")
    public String enroll(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/web/auth/login";
        }
        courseService.enrollUser(id, user.getId());
        return "redirect:/web/home";
    }
}
