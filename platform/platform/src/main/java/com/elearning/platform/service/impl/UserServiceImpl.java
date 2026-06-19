package com.elearning.platform.service.impl;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Role;
import com.elearning.platform.domain.User;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.exception.UnauthorizedException;
import com.elearning.platform.repository.UserRepository;
import com.elearning.platform.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
    }

    @Override
    @Transactional
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        user.setPassword(user.getPassword());
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (rawPassword.equals(user.getPassword())) {
            user.setPassword(rawPassword);
            return userRepository.save(user);
        }

        throw new UnauthorizedException("Invalid email or password");
    }

    @Override
    @Transactional
    public User updateUser(Long id, User updated) {
        User user = getUserById(id);

        if (userRepository.existsByEmailAndIdNot(updated.getEmail(), id)) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsernameAndIdNot(updated.getUsername(), id)) {
            throw new BadRequestException("Username already exists");
        }

        user.setEmail(updated.getEmail());
        user.setPassword(updated.getPassword());
        user.setRole(updated.getRole());
        user.setUsername(updated.getUsername());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<Course> getUserCourses(Long userId) {
        return getUserById(userId).getCourses();
    }

    @Override
    public Page<User> getUsersPaged(Pageable pageable, String sortBy, String sortDir) {
        if ("coursesCount".equals(sortBy)) {
            return "desc".equalsIgnoreCase(sortDir)
                    ? userRepository.findAllSortedByCoursesCountDesc(pageable)
                    : userRepository.findAllSortedByCoursesCountAsc(pageable);
        }
        return userRepository.findAll(pageable);
    }
}