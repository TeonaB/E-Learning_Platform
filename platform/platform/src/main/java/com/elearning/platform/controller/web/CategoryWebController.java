package com.elearning.platform.controller.web;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.CategoryDto;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.service.interf.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/admin/categories")
@RequiredArgsConstructor
public class CategoryWebController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        model.addAttribute("category", new CategoryDto());
        return "category/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        Category category = categoryService.getCategoryById(id);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        
        model.addAttribute("categoryId", id);
        model.addAttribute("category", categoryDto);
        return "category/form";
    }

    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("category") CategoryDto categoryDto,
                               BindingResult bindingResult,
                               @RequestParam(required = false) Long categoryId,
                               Model model,
                               HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }

        if (bindingResult.hasErrors()) {
            if (categoryId != null) {
                model.addAttribute("categoryId", categoryId);
            }
            return "category/form";
        }

        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        try {
            if (categoryId != null) {
                categoryService.updateCategory(categoryId, category);
            } else {
                categoryService.createCategory(category);
            }
            return "redirect:/web/admin/categories";
        } catch (BadRequestException ex) {
            model.addAttribute("saveError", ex.getMessage());
            if (categoryId != null) {
                model.addAttribute("categoryId", categoryId);
            }
            return "category/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/web/auth/login";
        }
        categoryService.deleteCategory(id);
        return "redirect:/web/admin/categories";
    }
}
