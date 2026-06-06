/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.service;

import com.nitefox.cloudattic.entity.User;
import com.nitefox.cloudattic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 *
 * @author NiTeFox
 */


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
    }

    @Test
    void register_ShouldEncodePasswordAndSaveUser() {
        when(passwordEncoder.encode("secret")).thenReturn("encodedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User registered = authService.register(user);

        assertThat(registered.getPassword()).isEqualTo("encodedSecret");
        assertThat(registered.getUsername()).isEqualTo("alice");
        assertThat(registered.getEmail()).isEqualTo("alice@example.com");
    }
}
