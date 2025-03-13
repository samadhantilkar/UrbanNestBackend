package com.project.UrbanNest.repository;

import com.project.UrbanNest.Config.TestContainerConfiguration;
import com.project.UrbanNest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestContainerConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp(){
        user=user.builder()
                .email("samadhan@gmail.com")
                .password("samadhan")
                .name("samadhan")
                .build();
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnUser() {


        //Arrange, Given
        userRepository.save(user);


        //Act, When
        Optional<User> userOptional=userRepository.findByEmail(user.getEmail());


        //Assert, Then
        assertThat(userOptional).isNotNull();
        assertThat(userOptional).isNotEmpty();
        assertThat(userOptional.get().getEmail()).isEqualTo(user.getEmail());
    }


    @Test
     void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyUser(){
//        Given
        String email="samadhan@gmail.com";
        //When
        Optional<User> userOptional=userRepository.findByEmail(email);

        //then
        assertThat(userOptional).isNotNull();
        assertThat(userOptional).isEmpty();
    }

}