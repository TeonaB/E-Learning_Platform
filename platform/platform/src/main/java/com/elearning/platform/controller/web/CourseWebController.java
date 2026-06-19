package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.CourseDto;
import com.elearning.platform.mapper.CourseMapper;
import com.elearning.platform.service.interf.CategoryService;
import com.elearning.platform.service.interf.CourseService;
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
public class CourseWebController {

    private final CourseService courseService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final CourseMapper courseMapper;

    // ADMIN: View courses list
    @GetMapping("/admin/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "course/list";
    }

    // ADMIN: Show create form
    @GetMapping("/admin/courses/create")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new CourseDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "course/form";
    }

    // ADMIN: Show edit form
    @GetMapping("/admin/courses/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        CourseDto courseDto = courseMapper.toCourseDto(course);
        
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
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            if (courseId != null) {
                model.addAttribute("courseId", courseId);
            }
            return "course/form";
        }

        Course course = courseMapper.toCourse(courseDto);
        if (courseId != null) {
            course.setId(courseId);
        }

        if (courseId != null) {
            courseService.updateCourse(courseId, course);
        } else {
            courseService.createCourse(course);
        }

        return "redirect:/web/admin/courses";
    }

    // ADMIN: Delete course
    @GetMapping("/admin/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/web/admin/courses";
    }

    // USER: View enrolled courses
    @GetMapping("/courses/my-courses")
    public String myCourses(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        List<Course> myCourses = userService.getUserCourses(user.getId());
        model.addAttribute("courses", myCourses);
        return "course/my-courses";
    }

    // USER: Enroll in course
    @PostMapping("/courses/{id}/enroll")
    public String enroll(@PathVariable Long id, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        courseService.enrollUser(id, user.getId());
        return "redirect:/web/home";
    }
}
