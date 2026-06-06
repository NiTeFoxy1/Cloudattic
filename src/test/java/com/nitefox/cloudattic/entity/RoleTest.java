/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.entity;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
/**
 *
 * @author NiTeFox
 */
class RoleTest {

    @Test
    void testNoArgsConstructor() {
        Role role = new Role();
        role.setName("ADMIN");
        assertThat(role.getName()).isEqualTo("ADMIN");
        assertThat(role.getId()).isNull(); // сеттера для id нет
    }

    @Test
    void testConstructorWithName() {
        Role role = new Role("USER");
        assertThat(role.getName()).isEqualTo("USER");
        assertThat(role.getUsers()).isEmpty();
        assertThat(role.getId()).isNull();
    }

    @Test
    void testGettersAndSetters() {
        Role role = new Role();
        role.setName("MANAGER");
        User user = new User();
        role.setUsers(Set.of(user));

        assertThat(role.getName()).isEqualTo("MANAGER");
        assertThat(role.getUsers()).containsExactly(user);
    }
}