package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Role;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.UserDto;
import com.elearning.platform.exception.BadRequestException;
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
@RequestMapping("/web/admin/users")
@RequiredArgsConstructor
public class UserAdminWebController {

    private final UserService userService;

    // ADMIN: View users list
    @GetMapping
    public String listUsers(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        model.addAttribute("users", userService.getAllUsers());
        return "user/list";
    }

    // ADMIN: Show create form
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        model.addAttribute("user", new UserDto());
        model.addAttribute("roles", Role.values());
        return "user/form";
    }

    // ADMIN: Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        User targetUser = userService.getUserById(id);
        UserDto userDto = new UserDto();
        userDto.setUsername(targetUser.getUsername());
        userDto.setEmail(targetUser.getEmail());
        userDto.setPassword(targetUser.getPassword());
        userDto.setRole(targetUser.getRole().name());

        model.addAttribute("userId", id);
        model.addAttribute("user", userDto);
        model.addAttribute("roles", Role.values());
        return "user/form";
    }

    // ADMIN: Save/Update user
    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") UserDto userDto,
                           BindingResult bindingResult,
                           @RequestParam(required = false) Long userId,
                           Model model,
                           HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }

        boolean hasOtherErrors = false;
        if (bindingResult.hasErrors()) {
            for (org.springframework.validation.FieldError error : bindingResult.getFieldErrors()) {
                if (error.getField().equals("password") && userId != null && (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty())) {
                    continue;
                }
                hasOtherErrors = true;
            }
            if (bindingResult.getGlobalErrorCount() > 0) {
                hasOtherErrors = true;
            }
        }

        if (hasOtherErrors) {
            System.out.println("Validation errors encountered during user save: " + bindingResult.getAllErrors());
            model.addAttribute("roles", Role.values());
            if (userId != null) {
                model.addAttribute("userId", userId);
            }
            return "user/form";
        }

        User targetUser = new User();
        targetUser.setUsername(userDto.getUsername());
        targetUser.setEmail(userDto.getEmail());
        
        if (userId != null && (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty())) {
            User existingUser = userService.getUserById(userId);
            targetUser.setPassword(existingUser.getPassword());
        } else {
            targetUser.setPassword(userDto.getPassword());
        }

        try {
            targetUser.setRole(Role.valueOf(userDto.getRole().toUpperCase().trim()));
        } catch (Exception e) {
            targetUser.setRole(Role.USER);
        }

        try {
            if (userId != null) {
                userService.updateUser(userId, targetUser);
            } else {
                userService.registerUser(targetUser);
            }
            return "redirect:/web/admin/users";
        } catch (BadRequestException ex) {
            model.addAttribute("saveError", ex.getMessage());
            model.addAttribute("roles", Role.values());
            if (userId != null) {
                model.addAttribute("userId", userId);
            }
            return "user/form";
        }
    }

    // ADMIN: Delete user
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        
        // Prevent deleting oneself
        if (user.getId().equals(id)) {
            return "redirect:/web/admin/users";
        }

        userService.deleteUser(id);
        return "redirect:/web/admin/users";
    }
}
