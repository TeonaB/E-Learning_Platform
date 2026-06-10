package com.elearning.platform.service.interf;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.User;

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
}