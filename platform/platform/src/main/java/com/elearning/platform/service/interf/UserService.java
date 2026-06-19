package com.elearning.platform.service.interf;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByEmail(String email);

    User registerUser(User user);

    User login(String email, String rawPassword);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    List<Course> getUserCourses(Long userId);

    Page<User> getUsersPaged(Pageable pageable, String sortBy, String sortDir);
}