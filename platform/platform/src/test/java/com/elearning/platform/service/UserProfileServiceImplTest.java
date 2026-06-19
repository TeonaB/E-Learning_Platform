package com.elearning.platform.service;

import com.elearning.platform.domain.User;
import com.elearning.platform.domain.UserProfile;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.UserProfileRepository;
import com.elearning.platform.repository.UserRepository;
import com.elearning.platform.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Test
    void testGetAllProfiles() {
        UserProfile p1 = new UserProfile();
        p1.setId(1L);
        p1.setFirstName("John");

        UserProfile p2 = new UserProfile();
        p2.setId(2L);
        p2.setFirstName("Jane");

        when(userProfileRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<UserProfile> profiles = userProfileService.getAllProfiles();
        assertEquals(2, profiles.size());
        assertEquals("John", profiles.get(0).getFirstName());
        verify(userProfileRepository, times(1)).findAll();
    }

    @Test
    void testGetProfileById_Success() {
        UserProfile p = new UserProfile();
        p.setId(1L);
        p.setFirstName("John");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(p));

        UserProfile found = userProfileService.getProfileById(1L);
        assertNotNull(found);
        assertEquals("John", found.getFirstName());
        verify(userProfileRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProfileById_NotFound() {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userProfileService.getProfileById(1L));
        verify(userProfileRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProfileByUserId_Success() {
        UserProfile p = new UserProfile();
        p.setId(1L);
        p.setFirstName("John");

        when(userProfileRepository.findByUser_Id(10L)).thenReturn(Optional.of(p));

        UserProfile found = userProfileService.getProfileByUserId(10L);
        assertNotNull(found);
        assertEquals("John", found.getFirstName());
    }

    @Test
    void testGetProfileByUserId_NotFound() {
        when(userProfileRepository.findByUser_Id(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userProfileService.getProfileByUserId(10L));
    }

    @Test
    void testCreateProfile_Success() {
        User user = new User();
        user.setId(10L);

        UserProfile profile = new UserProfile();
        profile.setFirstName("John");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userProfileRepository.existsByUser_Id(10L)).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArgument(0));

        UserProfile created = userProfileService.createProfile(10L, profile);
        assertNotNull(created);
        assertEquals(user, created.getUser());
        assertEquals("John", created.getFirstName());
        verify(userProfileRepository, times(1)).save(profile);
    }

    @Test
    void testCreateProfile_UserNotFound() {
        UserProfile profile = new UserProfile();

        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userProfileService.createProfile(10L, profile));
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void testCreateProfile_AlreadyExists() {
        User user = new User();
        user.setId(10L);
        UserProfile profile = new UserProfile();

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userProfileRepository.existsByUser_Id(10L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userProfileService.createProfile(10L, profile));
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void testUpdateProfile_Success() {
        UserProfile existing = new UserProfile();
        existing.setId(1L);
        existing.setFirstName("OldName");

        UserProfile updatedInfo = new UserProfile();
        updatedInfo.setFirstName("NewName");
        updatedInfo.setLastName("LastName");
        updatedInfo.setPhoneNumber("123456");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArgument(0));

        UserProfile result = userProfileService.updateProfile(1L, updatedInfo);
        assertEquals("NewName", result.getFirstName());
        assertEquals("LastName", result.getLastName());
        assertEquals("123456", result.getPhoneNumber());
        verify(userProfileRepository, times(1)).save(existing);
    }

    @Test
    void testDeleteProfile_Success() {
        when(userProfileRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userProfileRepository).deleteById(1L);

        userProfileService.deleteProfile(1L);
        verify(userProfileRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProfile_NotFound() {
        when(userProfileRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userProfileService.deleteProfile(1L));
        verify(userProfileRepository, never()).deleteById(anyLong());
    }
}
