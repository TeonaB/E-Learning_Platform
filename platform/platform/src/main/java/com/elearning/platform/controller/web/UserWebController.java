package com.elearning.platform.controller.web;

import com.elearning.platform.domain.User;
import com.elearning.platform.dto.UserDto;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.mapper.UserMapper;
import com.elearning.platform.service.interf.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/auth")
@RequiredArgsConstructor
public class UserWebController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDto userDto,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            User user = userMapper.toUser(userDto);
            userService.registerUser(user);
            return "redirect:/web/auth/login?registered=true";
        } catch (BadRequestException ex) {
            model.addAttribute("registerError", ex.getMessage());
            return "register";
        }
    }
}
