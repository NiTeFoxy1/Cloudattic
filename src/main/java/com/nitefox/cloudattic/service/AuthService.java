/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.service;

import com.nitefox.cloudattic.entity.User;
import com.nitefox.cloudattic.repository.UserRepository;
import static java.lang.Math.log;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
/**
 *
 * @author NiTeFox
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        log.info("Registering new user: {}", user.getUsername());
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );
        User saved = userRepository.save(user);
        log.info("User registered successfully with id: {}", saved.getId());
        return saved;
    }
}