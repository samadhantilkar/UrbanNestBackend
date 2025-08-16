package com.project.UrbanNest.service.imp;

import com.project.UrbanNest.dto.ProfileUpdateRequestDto;
import com.project.UrbanNest.dto.UserDto;
import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.repository.UserRepository;
import com.project.UrbanNest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.project.UrbanNest.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService , UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User getUserById(Long id) {

        return userRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("User not found with Id: "+id ));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        User user=getCurrentUser();
        if(profileUpdateRequestDto.getDateOfBirth() != null) user.setDateOfBirth(profileUpdateRequestDto.getDateOfBirth());
        if(profileUpdateRequestDto.getGender() != null ) user.setGender(profileUpdateRequestDto.getGender());
        if(profileUpdateRequestDto.getName() !=null ) user.setName(profileUpdateRequestDto.getName());

        userRepository.save(user);

    }

    @Override
    public UserDto getMyProfile() {
        User user=getCurrentUser();
        log.info("Getting the profile for user with Id: {}",user.getId());
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }
}
