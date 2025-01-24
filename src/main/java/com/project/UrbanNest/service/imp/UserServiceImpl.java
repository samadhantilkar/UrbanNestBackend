package com.project.UrbanNest.service.imp;

import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.repository.UserRepository;
import com.project.UrbanNest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService , UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {

        return userRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("User not found with Id: "+id ));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }
}
