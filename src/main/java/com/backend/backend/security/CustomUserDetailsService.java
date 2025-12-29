package com.backend.backend.security;

import com.backend.backend.entity.User.User;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try to find by email first
        Optional<User> userOptional = userRepository.findByEmail(identifier);

        // If not found by email, try username
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByUsername(identifier);
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new CustomUserDetails(user);
        } else {
            throw new UsernameNotFoundException("User not found with identifier: " + identifier);
        }
    }
}