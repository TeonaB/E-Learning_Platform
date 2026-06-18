package com.elearning.platform.controller.web;

import com.elearning.platform.domain.User;
import com.elearning.platform.dto.LoginRequestDto;
import com.elearning.platform.dto.UserDto;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.UnauthorizedException;
import com.elearning.platform.service.interf.UserService;
import jakarta.servlet.http.HttpSession;
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

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequest,
                        BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        try {
            User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            session.setAttribute("currentUser", user);
            return "redirect:/web/home";
        } catch (UnauthorizedException ex) {
            model.addAttribute("loginError", "Email sau parolă incorectă!");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDto userDto,
                           BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setPassword(userDto.getPassword());
            
            User savedUser = userService.registerUser(user);
            session.setAttribute("currentUser", savedUser);
            return "redirect:/web/home";
        } catch (BadRequestException ex) {
            model.addAttribute("registerError", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("currentUser");
        session.invalidate();
        return "redirect:/web/home";
    }
}
