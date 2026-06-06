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

class ShareLinkTest {

    @Test
    void testGettersAndSetters() {
        ShareLink link = new ShareLink();
        link.setId(1L);
        link.setToken("abc123");
        link.setExpirationDate(LocalDateTime.now().plusDays(1));
        link.setPublicAccess(true);

        FileEntity file = new FileEntity();
        link.setFile(file);

        assertThat(link.getId()).isEqualTo(1L);
        assertThat(link.getToken()).isEqualTo("abc123");
        assertThat(link.getExpirationDate()).isAfter(LocalDateTime.now());
        assertThat(link.isPublicAccess()).isTrue();
        assertThat(link.getFile()).isEqualTo(file);
    }
}
