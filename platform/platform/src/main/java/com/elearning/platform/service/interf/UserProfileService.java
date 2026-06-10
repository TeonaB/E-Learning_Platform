package com.elearning.platform.service.interf;

import com.elearning.platform.domain.UserProfile;

import java.util.List;

public interface UserProfileService {

    List<UserProfile> getAllProfiles();

    UserProfile getProfileById(Long id);

    UserProfile getProfileByUserId(Long userId);

    UserProfile createProfile(Long userId, UserProfile profile);

    UserProfile updateProfile(Long id, UserProfile profile);

    void deleteProfile(Long id);
}