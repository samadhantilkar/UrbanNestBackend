package com.project.UrbanNest.security;


import com.project.UrbanNest.dto.LoginDto;
import com.project.UrbanNest.dto.SignUpRequestDto;
import com.project.UrbanNest.dto.UserDto;
import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.entity.enums.Role;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;


    public UserDto signUp(SignUpRequestDto signUpRequestDto){
        User user=userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);

        if(null != user){
            throw new RuntimeException("User is already present with same email id:"+signUpRequestDto.getEmail());
        }
        User newUser=modelMapper.map(signUpRequestDto,User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        userRepository.save(newUser);

        return modelMapper.map(newUser,UserDto.class);
    }

    public String[] login(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail()
                        ,loginDto.getPassword())
        );

        User user= (User) authentication.getPrincipal();

        String[] tokens=new String[2];
        tokens[0]= jwtService.generateAccessToken(user);
        tokens[1] = jwtService.generateRefreshToken(user);

        return tokens;
    }

    public String refreshToken(String refreshToken){
        Long id=jwtService.getUserIdFromToken(refreshToken);

        User user=userRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException("User not found with id: "+id));

        return jwtService.generateAccessToken(user);
    }

}
