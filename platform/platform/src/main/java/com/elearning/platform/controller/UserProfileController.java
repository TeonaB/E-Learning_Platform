package com.elearning.platform.controller;

import com.elearning.platform.domain.UserProfile;
import com.elearning.platform.dto.UserProfileDto;
import com.elearning.platform.dto.UserProfileResponseDto;
import com.elearning.platform.mapper.ResponseMapper;
import com.elearning.platform.service.interf.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<List<UserProfileResponseDto>> getAllProfiles() {
        return ResponseEntity.ok(userProfileService.getAllProfiles().stream().map(ResponseMapper::toUserProfileDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> getProfileById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ResponseMapper.toUserProfileDto(userProfileService.getProfileById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileResponseDto> getProfileByUserId(@PathVariable @Positive Long userId) {
        return ResponseEntity.ok(ResponseMapper.toUserProfileDto(userProfileService.getProfileByUserId(userId)));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> createProfile(@PathVariable @Positive Long userId,
                                                                @Valid @RequestBody UserProfileDto profileDto) {
        UserProfile profile = new UserProfile();
        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setPhoneNumber(profileDto.getPhoneNumber());

        UserProfile saved = userProfileService.createProfile(userId, profile);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(ResponseMapper.toUserProfileDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> updateProfile(@PathVariable @Positive Long id,
                                                                @Valid @RequestBody UserProfileDto profileDto) {
        UserProfile profile = new UserProfile();
        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setPhoneNumber(profileDto.getPhoneNumber());

        UserProfile updated = userProfileService.updateProfile(id, profile);
        return ResponseEntity.ok(ResponseMapper.toUserProfileDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable @Positive Long id) {
        userProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}