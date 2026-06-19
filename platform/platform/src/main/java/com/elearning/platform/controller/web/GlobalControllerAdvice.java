package com.elearning.platform.controller.web;

import com.elearning.platform.domain.User;
import com.elearning.platform.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.elearning.platform.controller.web")
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            try {
                return userService.getUserByEmail(authentication.getName());
            } catch (Exception e) {
                // User might not exist in the database (e.g. mock test user)
                return null;
            }
        }
        return null;
    }
}
