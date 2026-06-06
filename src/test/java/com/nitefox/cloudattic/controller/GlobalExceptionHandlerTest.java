/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author NiTeFox
 */

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handle403_shouldReturnRedirectToLinkExpired() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.FORBIDDEN);
        String result = handler.handle(ex);
        assertThat(result).isEqualTo("redirect:/link-expired");
    }

    @Test
    void handle404_shouldReturnRedirectToLinkExpired() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND);
        String result = handler.handle(ex);
        assertThat(result).isEqualTo("redirect:/link-expired");
    }

    @Test
    void handleOtherStatus_shouldReturnErrorView() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST);
        String result = handler.handle(ex);
        assertThat(result).isEqualTo("error");
    }
}