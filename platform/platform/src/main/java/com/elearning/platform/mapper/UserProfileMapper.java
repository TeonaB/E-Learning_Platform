package com.elearning.platform.mapper;

import com.elearning.platform.domain.UserProfile;
import com.elearning.platform.dto.UserProfileDto;
import com.elearning.platform.dto.UserProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileMapper {

    private final ModelMapper modelMapper;

    public UserProfileResponseDto toUserProfileResponseDto(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        UserProfileResponseDto dto = modelMapper.map(profile, UserProfileResponseDto.class);
        dto.setUserId(profile.getUser() != null ? profile.getUser().getId() : null);
        return dto;
    }

    public UserProfileDto toUserProfileDto(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        return modelMapper.map(profile, UserProfileDto.class);
    }

    public UserProfile toUserProfile(UserProfileDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, UserProfile.class);
    }
}
