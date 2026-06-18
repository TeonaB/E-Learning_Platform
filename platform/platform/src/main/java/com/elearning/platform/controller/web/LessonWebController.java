package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Lesson;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.LessonDto;
import com.elearning.platform.service.interf.CourseService;
import com.elearning.platform.service.interf.LessonService;
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
public class LessonWebController {

    private final LessonService lessonService;
    private final CourseService courseService;
    private final UserService userService;

    // Student/Admin: View lessons of a specific course
    @GetMapping("/courses/{courseId}/lessons")
    public String viewCourseLessons(@PathVariable Long courseId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/web/auth/login";
        }

        Course course = courseService.getCourseById(courseId);
        
        // Security check: Only enrolled users or ADMIN can view the lessons
        if (!user.getRole().name().equals("ADMIN")) {
            User freshUser = userService.getUserById(user.getId());
            boolean isEnrolled = freshUser.getCourses().stream().anyMatch(c -> c.getId().equals(courseId));
            if (!isEnrolled) {
                return "redirect:/web/home"; // Not enrolled, send back to home
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("lessons", lessonService.getLessonsByCourse(courseId));
        return "lesson/list";
    }

    // ADMIN: Show create form for a lesson under a course
    @GetMapping("/admin/lessons/create/{courseId}")
    public String showCreateForm(@PathVariable Long courseId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        
        LessonDto lessonDto = new LessonDto();
        model.addAttribute("lesson", lessonDto);
        model.addAttribute("courseId", courseId);
        return "lesson/form";
    }

    // ADMIN: Show edit form
    @GetMapping("/admin/lessons/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        
        Lesson lesson = lessonService.getLessonById(id);
        LessonDto lessonDto = new LessonDto();
        lessonDto.setTitle(lesson.getTitle());
        lessonDto.setContentUrl(lesson.getContentUrl());
        lessonDto.setDurationMinutes(lesson.getDurationMinutes());
        
        model.addAttribute("lessonId", id);
        model.addAttribute("lesson", lessonDto);
        model.addAttribute("courseId", lesson.getCourse().getId());
        return "lesson/form";
    }

    // ADMIN: Save/Update lesson
    @PostMapping("/admin/lessons/save")
    public String saveLesson(@Valid @ModelAttribute("lesson") LessonDto lessonDto,
                             BindingResult bindingResult,
                             @RequestParam Long courseId,
                             @RequestParam(required = false) Long lessonId,
                             Model model,
                             HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("courseId", courseId);
            if (lessonId != null) {
                model.addAttribute("lessonId", lessonId);
            }
            return "lesson/form";
        }

        Lesson lesson = new Lesson();
        lesson.setTitle(lessonDto.getTitle());
        lesson.setContentUrl(lessonDto.getContentUrl());
        lesson.setDurationMinutes(lessonDto.getDurationMinutes());

        if (lessonId != null) {
            lessonService.updateLesson(lessonId, lesson);
        } else {
            lessonService.createLesson(courseId, lesson);
        }

        return "redirect:/web/courses/" + courseId + "/lessons";
    }

    // ADMIN: Delete lesson
    @GetMapping("/admin/lessons/delete/{id}")
    public String deleteLesson(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        Lesson lesson = lessonService.getLessonById(id);
        Long courseId = lesson.getCourse().getId();
        lessonService.deleteLesson(id);
        return "redirect:/web/courses/" + courseId + "/lessons";
    }
}
