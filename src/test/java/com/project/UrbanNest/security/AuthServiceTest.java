package com.project.UrbanNest.security;

import com.project.UrbanNest.Config.TestContainerConfiguration;
import com.project.UrbanNest.dto.LoginDto;
import com.project.UrbanNest.dto.SignUpRequestDto;
import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.entity.enums.Gender;
import com.project.UrbanNest.entity.enums.Role;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Spy
    private ModelMapper modelMapper;

    private User user;
    private SignUpRequestDto signUpRequestDto;

    @BeforeEach
    void setUp(){
        user=User.builder()
                .name("samadhan")
                .password("samadhan")
                .email("samadhan123123123@gmail.com")
                .roles(Set.of(Role.GUEST,Role.HOTEL_MANAGER))
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(2004,9,20))
                .build();


        signUpRequestDto=modelMapper.map(user,SignUpRequestDto.class);

    }

    @Test
    void testSignUpUser_WhenValidUser_ThenCreateNewUser() {

        //assign
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());   //Stubbing

//        when(userRepository.save(any(User.class))).thenReturn(user);
        //act

//        UserDto userDto = authService.signUp(signUpRequestDto);

        //assert

//        assertThat(userDto).isNotNull();
//        assertThat(userDto.getEmail()).isEqualTo(signUpRequestDto.getEmail());
//        verify(userRepository).save(any());
//        ArgumentCaptor<User> userArgumentCaptor= ArgumentCaptor.forClass(User.class);
//        verify(userRepository).save(userArgumentCaptor.capture());

//        User capturedUser=userArgumentCaptor.getValue();

//        assertThat(capturedUser.getEmail()).isEqualTo(user.getEmail());

    }

    @Test
    void testLoginUser_whenUserIsPresent_thenThrowException(){

        LoginDto loginDto= LoginDto.builder()
                .email("samadhan")
                .password("samadhan")
                .build();

        //arrange

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new ResourceNotFoundException("User is already present with same email id:"+loginDto.getEmail()));

        //act & assert

        assertThatThrownBy(()-> authService.login(loginDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User is already present with same email id:"+loginDto.getEmail());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository,never()).save(any());
    }

    @Test
    void login() {
    }

    @Test
    void refreshToken() {
    }
}