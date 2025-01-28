package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.ProfileUpdateRequestDto;
import com.project.UrbanNest.dto.UserDto;
import com.project.UrbanNest.entity.User;

public interface UserService {

    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
