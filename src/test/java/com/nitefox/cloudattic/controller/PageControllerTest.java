/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.controller;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


/**
 *
 * @author NiTeFox
 */
// PageControllerTest.java

class PageControllerTest {

    private final PageController controller = new PageController();

    @Test
    void index_shouldReturnHomeView() {
        assertThat(controller.index()).isEqualTo("home");
    }

    @Test
    void about_shouldReturnAboutView() {
        assertThat(controller.about()).isEqualTo("about");
    }

    @Test
    void contacts_shouldReturnContactsView() {
        assertThat(controller.contacts()).isEqualTo("contacts");
    }

    @Test
    void login_shouldReturnLoginView() {
        assertThat(controller.login()).isEqualTo("login");
    }

    @Test
    void register_shouldReturnRegisterView() {
        assertThat(controller.register()).isEqualTo("register");
    }

    @Test
    void find_shouldReturnFindView() {
        assertThat(controller.find()).isEqualTo("find");
    }

    @Test
    void linkExpired_shouldReturnExpiredLinkView() {
        assertThat(controller.linkExpired()).isEqualTo("expired_link");
    }
}