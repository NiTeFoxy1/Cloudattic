/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.nitefox.cloudattic.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
/**
 *
 * @author NiTeFox
 */

class UserTest {

    @Test
    void testGettersAndSettersForAccessibleFields() {
        User user = new User();
        user.setId(100L);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPass");
        user.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));

        assertThat(user.getId()).isEqualTo(100L);
        assertThat(user.getUsername()).isEqualTo("john_doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("encodedPass");
        assertThat(user.getCreatedAt()).isEqualTo("2024-01-01T00:00");
    }

    @Test
    void testDefaultCreatedAtIsNotNull() {
        User user = new User();
        assertThat(user.getCreatedAt()).isNotNull();
    }
}