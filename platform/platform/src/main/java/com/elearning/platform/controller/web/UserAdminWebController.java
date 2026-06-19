package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Role;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.UserDto;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.mapper.UserMapper;
import com.elearning.platform.service.interf.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final UserMapper userMapper;

    // ADMIN: View users list
    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = "coursesCount".equals(sortBy) 
                ? Sort.unsorted() 
                : Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userService.getUsersPaged(pageable, sortBy, sortDir);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "user/list";
    }

    // ADMIN: Show create form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("roles", Role.values());
        return "user/form";
    }

    // ADMIN: Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User targetUser = userService.getUserById(id);
        UserDto userDto = userMapper.toUserDto(targetUser);

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
                           Model model) {

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

        User targetUser = userMapper.toUser(userDto);
        if (userId != null && (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty())) {
            User existingUser = userService.getUserById(userId);
            targetUser.setPassword(existingUser.getPassword());
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
    public String deleteUser(@PathVariable Long id, java.security.Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        
        // Prevent deleting oneself
        if (user.getId().equals(id)) {
            return "redirect:/web/admin/users";
        }

        userService.deleteUser(id);
        return "redirect:/web/admin/users";
    }
}
