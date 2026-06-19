package com.elearning.platform.controller.web;

import com.elearning.platform.domain.User;
import com.elearning.platform.domain.UserProfile;
import com.elearning.platform.dto.UserProfileDto;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.mapper.UserProfileMapper;
import com.elearning.platform.service.interf.UserProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/profile")
@RequiredArgsConstructor
public class UserProfileWebController {

    private final UserProfileService userProfileService;
    private final UserProfileMapper userProfileMapper;

    // View Profile page (details & form)
    @GetMapping
    public String showProfile(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/web/auth/login";
        }

        UserProfileDto profileDto = new UserProfileDto();
        boolean isNew = true;
        Long profileId = null;

        try {
            UserProfile profile = userProfileService.getProfileByUserId(currentUser.getId());
            profileDto = userProfileMapper.toUserProfileDto(profile);
            isNew = false;
            profileId = profile.getId();
        } catch (ResourceNotFoundException ex) {
            // Profile does not exist yet; will create a new one
        }

        model.addAttribute("profile", profileDto);
        model.addAttribute("isNew", isNew);
        model.addAttribute("profileId", profileId);
        return "user/profile";
    }

    // Save/Update Profile
    @PostMapping("/save")
    public String saveProfile(@Valid @ModelAttribute("profile") UserProfileDto profileDto,
                              BindingResult bindingResult,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/web/auth/login";
        }

        boolean isNew = true;
        Long profileId = null;
        UserProfile existingProfile = null;

        try {
            existingProfile = userProfileService.getProfileByUserId(currentUser.getId());
            isNew = false;
            profileId = existingProfile.getId();
        } catch (ResourceNotFoundException ex) {
            // New profile
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", isNew);
            model.addAttribute("profileId", profileId);
            return "user/profile";
        }

        try {
            UserProfile profile = userProfileMapper.toUserProfile(profileDto);
            if (isNew) {
                userProfileService.createProfile(currentUser.getId(), profile);
                redirectAttributes.addFlashAttribute("successMessage", "Profilul a fost creat cu succes!");
            } else {
                userProfileService.updateProfile(profileId, profile);
                redirectAttributes.addFlashAttribute("successMessage", "Profilul a fost actualizat cu succes!");
            }
            return "redirect:/web/profile";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "A apărut o eroare la salvarea profilului: " + ex.getMessage());
            model.addAttribute("isNew", isNew);
            model.addAttribute("profileId", profileId);
            return "user/profile";
        }
    }

    // Delete Profile
    @PostMapping("/delete")
    public String deleteProfile(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/web/auth/login";
        }

        try {
            UserProfile profile = userProfileService.getProfileByUserId(currentUser.getId());
            userProfileService.deleteProfile(profile.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Profilul a fost șters cu succes!");
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Profilul nu a fost găsit pentru a fi șters.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "A apărut o eroare la ștergerea profilului: " + ex.getMessage());
        }

        return "redirect:/web/profile";
    }
}
