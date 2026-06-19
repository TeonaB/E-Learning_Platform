package com.elearning.platform.mapper;

import com.elearning.platform.domain.User;
import com.elearning.platform.domain.Role;
import com.elearning.platform.dto.UserDto;
import com.elearning.platform.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserResponseDto toUserResponseDto(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        return dto;
    }

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = modelMapper.map(user, UserDto.class);
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        return dto;
    }

    public User toUser(UserDto dto) {
        if (dto == null) {
            return null;
        }
        User user = modelMapper.map(dto, User.class);
        if (dto.getRole() != null) {
            try {
                user.setRole(Role.valueOf(dto.getRole().toUpperCase().trim()));
            } catch (Exception e) {
                user.setRole(Role.USER);
            }
        }
        return user;
    }
}
