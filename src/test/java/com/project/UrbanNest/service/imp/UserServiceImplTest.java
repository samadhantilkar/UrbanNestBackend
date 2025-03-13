package com.project.UrbanNest.service.imp;

import com.project.UrbanNest.Config.TestContainerConfiguration;
import com.project.UrbanNest.entity.User;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper;


    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp(){
        mockUser=User.builder()
                .name("Samadhan")
                .email("abc@gmail.com")
                .password("samadhan")
                .build();
    }

    @Test
    void testGetUserById_WhenUserEmailIsPresent_ThenReturnUserDto() {
        // assign
        Long id=1L;

        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        //act

        User user=userService.getUserById(id);

        //assert


        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(mockUser.getEmail());

//        verify(userRepository).findByEmail(mockUser.getEmail());
//        verify(userRepository,times(1)).findByEmail(mockUser.getEmail());
//        verify(userRepository,atLeast(1)).findByEmail(mockUser.getEmail());
//        verify(userRepository,atMost(1)).findByEmail(mockUser.getEmail());
//        verify(userRepository,only()).findByEmail(mockUser.getEmail());



    }

    @Test
    void updateProfile() {
    }

    @Test
    void getMyProfile() {
    }

    @Test
    void loadUserByUsername() {
    }
}