package com.project.UrbanNest.controller;

import com.project.UrbanNest.Config.TestContainerConfiguration;
import com.project.UrbanNest.dto.UserDto;
import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.entity.enums.Gender;
import com.project.UrbanNest.entity.enums.Role;
import com.project.UrbanNest.repository.UserRepository;
import com.project.UrbanNest.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureWebTestClient(timeout = "100000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfiguration.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerTestIT {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    private User testUser;

    private UserDto testUserDto;

    private String token;

    private User savedUser;

    @BeforeEach
    void setUp(){
        testUser =User.builder()
                .name("samadhan")
                .password("samadhan")
                .email("samadhan09@gmail.com")
                .roles(Set.of(Role.GUEST,Role.HOTEL_MANAGER))
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(2004,9,20))
                .build();

        savedUser=userRepository.save(testUser);

        token=jwtService.generateRefreshToken(testUser);

//        UserDetails userDetails= org.springframework.security.core.userdetails.User.builder()
//                .username(savedUser.getUsername())
//                .password(savedUser.getPassword())
//                .roles("GUEST","HOTEL_MANAGER")
//                .build();
//
//        SecurityContextHolder.getContext().setAuthentication(
//                new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities())
//        );


        testUserDto=UserDto.builder()
                .name("samadhan")
                .email("samadhan09@gmail.com")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(2004,9,20))
                .build();

        userRepository.deleteAll();
    }

    @Test
    void updateProfile() {
    }

    @Test
    void getMyBookings() {
    }

    @Test
//    @WithMockUser(username="samadhan@gmail.com",roles={"GUEST","HOTEL_MANAGER"})
    void testGetMyProfile_Success() {
        User savedUser=userRepository.save(testUser);

        webTestClient.get().uri("/users/profile")
                .header("Authorizaiton","Bearer"+token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
//                .isEqualTo(testUser)  //check base on the equal method define in user entity
                .value(testUserDto ->{
                    assertThat(testUserDto.getEmail()).isEqualTo(savedUser.getEmail());
                    assertThat(testUserDto.getName()).isEqualTo(savedUser.getName());
                });
    }

    @Test
    void testGetMyProfile_Failure(){
        User user=User.builder()
                .email("samadhan@gmail.com")
                .password("samadhan")
                .gender(Gender.MALE)
                .name("Samadhan")
                .build();

        token= jwtService.generateRefreshToken(user);

        webTestClient.get().uri("users/profile")
                .header("Authorization","Bearer"+token)
                .exchange()
                .expectStatus().isNotFound();


    }
}