package com.nitefox.cloudattic.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringJUnitConfig
@Import(SecurityConfig.class)
class SecurityConfigTest {
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Autowired(required = false)
    private AuthenticationProvider authenticationProvider;

    @Autowired(required = false)
    private SecurityFilterChain securityFilterChain;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        assertThat(securityConfig).isNotNull();
        assertThat(securityConfig.passwordEncoder()).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void authenticationProvider_ShouldBeCreated() {
        assertThat(authenticationProvider).isNotNull();
    }

    @Test
    void securityFilterChain_ShouldBeCreated() {
        assertThat(securityFilterChain).isNotNull();
    }
}