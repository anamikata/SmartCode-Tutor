
package com.smartcode.tutor.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smartcode.tutor.entity.User;
import com.smartcode.tutor.repository.UserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DatabaseUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(
                        usernameOrEmail.toLowerCase()))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found."));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
