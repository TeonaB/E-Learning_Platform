package com.elearning.platform.service;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Role;
import com.elearning.platform.domain.User;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.exception.UnauthorizedException;
import com.elearning.platform.repository.UserRepository;
import com.elearning.platform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetAllUsers() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<User> list = userService.getAllUsers();
        assertEquals(1, list.size());
    }

    @Test
    void testGetUserById_Success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getUserById(1L);
        assertNotNull(found);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testGetUserByEmail_Success() {
        User user = new User();
        user.setEmail("test@email.com");
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        User found = userService.getUserByEmail("test@email.com");
        assertNotNull(found);
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("test@email.com"));
    }

    @Test
    void testRegisterUser_Success() {
        User user = new User();
        user.setEmail("new@email.com");
        user.setUsername("newuser");
        user.setPassword("pass");

        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registered = userService.registerUser(user);
        assertNotNull(registered);
        assertEquals(Role.USER, registered.getRole());
    }

    @Test
    void testRegisterUser_EmailExists() {
        User user = new User();
        user.setEmail("exists@email.com");

        when(userRepository.existsByEmail("exists@email.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(user));
    }

    @Test
    void testRegisterUser_UsernameExists() {
        User user = new User();
        user.setEmail("new@email.com");
        user.setUsername("existsuser");

        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("existsuser")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(user));
    }

    @Test
    void testLogin_Success() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("password");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User loggedIn = userService.login("test@email.com", "password");
        assertNotNull(loggedIn);
    }

    @Test
    void testLogin_InvalidEmail() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> userService.login("test@email.com", "wrong"));
    }

    @Test
    void testLogin_InvalidPassword() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("password");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> userService.login("test@email.com", "wrong"));
    }

    @Test
    void testUpdateUser_Success() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("old@email.com");
        existing.setUsername("olduser");

        User updated = new User();
        updated.setEmail("new@email.com");
        updated.setUsername("newuser");
        updated.setPassword("newpass");
        updated.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("new@email.com", 1L)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdNot("newuser", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, updated);
        assertEquals("new@email.com", result.getEmail());
        assertEquals("newuser", result.getUsername());
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    void testUpdateUser_EmailExists() {
        User existing = new User();
        existing.setId(1L);

        User updated = new User();
        updated.setEmail("exists@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("exists@email.com", 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.updateUser(1L, updated));
    }

    @Test
    void testUpdateUser_UsernameExists() {
        User existing = new User();
        existing.setId(1L);

        User updated = new User();
        updated.setEmail("new@email.com");
        updated.setUsername("existsuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("new@email.com", 1L)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdNot("existsuser", 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.updateUser(1L, updated));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void testGetUserCourses() {
        User user = new User();
        user.setId(1L);
        user.setCourses(Collections.singletonList(new Course()));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<Course> courses = userService.getUserCourses(1L);
        assertEquals(1, courses.size());
    }
}
