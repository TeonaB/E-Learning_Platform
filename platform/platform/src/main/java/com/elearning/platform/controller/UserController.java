package com.elearning.platform.controller;

import com.elearning.platform.domain.Role;
import com.elearning.platform.domain.User;
import com.elearning.platform.dto.CourseResponseDto;
import com.elearning.platform.dto.LoginRequestDto;
import com.elearning.platform.dto.UserDto;
import com.elearning.platform.dto.UserResponseDto;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.mapper.ResponseMapper;
import com.elearning.platform.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(ResponseMapper::toUserDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ResponseMapper.toUserDto(userService.getUserById(id)));
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponseDto> getByEmail(@RequestParam @NotBlank @Email String email) {
        return ResponseEntity.ok(ResponseMapper.toUserDto(userService.getUserByEmail(email)));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        if (userDto.getRole() != null && !userDto.getRole().isBlank()) {
            try {
                user.setRole(Role.valueOf(userDto.getRole().trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Role must be USER or ADMIN");
            }
        }

        User saved = userService.registerUser(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(ResponseMapper.toUserDto(saved));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        User loggedIn = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(ResponseMapper.toUserDto(loggedIn));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable @Positive Long id,
                                                      @Valid @RequestBody UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        if (userDto.getRole() != null && !userDto.getRole().isBlank()) {
            try {
                user.setRole(Role.valueOf(userDto.getRole().trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Role must be USER or ADMIN");
            }
        } else {
            user.setRole(Role.USER);
        }

        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(ResponseMapper.toUserDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseResponseDto>> getUserCourses(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(userService.getUserCourses(id).stream().map(ResponseMapper::toCourseDto).toList());
    }
}