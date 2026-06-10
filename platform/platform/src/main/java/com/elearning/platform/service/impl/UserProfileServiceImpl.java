package com.elearning.platform.service.impl;

import com.elearning.platform.domain.User;
import com.elearning.platform.domain.UserProfile;
import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.repository.UserProfileRepository;
import com.elearning.platform.repository.UserRepository;
import com.elearning.platform.service.interf.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Override
    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    @Override
    public UserProfile getProfileById(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id " + id));
    }

    @Override
    public UserProfile getProfileByUserId(Long userId) {
        return userProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user id " + userId));
    }

    @Override
    @Transactional
    public UserProfile createProfile(Long userId, UserProfile profile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        if (userProfileRepository.existsByUser_Id(userId)) {
            throw new BadRequestException("Profile already exists for user id " + userId);
        }
        profile.setUser(user);

        return userProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public UserProfile updateProfile(Long id, UserProfile updated) {
        UserProfile profile = getProfileById(id);

        profile.setFirstName(updated.getFirstName());
        profile.setLastName(updated.getLastName());
        profile.setPhoneNumber(updated.getPhoneNumber());

        return userProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(Long id) {
        if (!userProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profile not found with id " + id);
        }
        userProfileRepository.deleteById(id);
    }
}