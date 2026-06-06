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

class DownloadHistoryTest {

    @Test
    void testPrePersistSetsDownloadTime() {
        DownloadHistory history = new DownloadHistory();
        history.prePersist(); // симулируем вызов до сохранения
        assertThat(history.getDownloadTime()).isNotNull();
        assertThat(history.getDownloadTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void testGettersAndSetters() {
        DownloadHistory history = new DownloadHistory();
        history.setId(1L);
        history.setIpAddress("192.168.1.1");
        history.setDownloadTime(LocalDateTime.of(2025, 1, 1, 12, 0));

        FileEntity file = new FileEntity();
        history.setFile(file);
        User user = new User();
        history.setUser(user);

        assertThat(history.getId()).isEqualTo(1L);
        assertThat(history.getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(history.getDownloadTime()).isEqualTo("2025-01-01T12:00");
        assertThat(history.getFile()).isEqualTo(file);
        assertThat(history.getUser()).isEqualTo(user);
    }

    @Test
    void testAllArgsConstructor() {
        FileEntity file = new FileEntity();
        User user = new User();
        LocalDateTime time = LocalDateTime.now();
        DownloadHistory history = new DownloadHistory(1L, time, "10.0.0.1", file, user);

        assertThat(history.getId()).isEqualTo(1L);
        assertThat(history.getDownloadTime()).isEqualTo(time);
        assertThat(history.getIpAddress()).isEqualTo("10.0.0.1");
        assertThat(history.getFile()).isEqualTo(file);
        assertThat(history.getUser()).isEqualTo(user);
    }
}