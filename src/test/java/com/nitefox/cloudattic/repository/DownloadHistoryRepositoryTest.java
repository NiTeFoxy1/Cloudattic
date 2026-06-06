/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.repository;

import com.nitefox.cloudattic.entity.DownloadHistory;
import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author NiTeFox
 */

@DataJpaTest
class DownloadHistoryRepositoryTest {

    @Autowired
    private DownloadHistoryRepository downloadHistoryRepository;

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindById_ShouldWork() {
        User user = new User();
        user.setUsername("downloader");
        user.setEmail("down@ex.com");
        user.setPassword("pass");
        user = userRepository.save(user);

        FileEntity file = new FileEntity();
        file.setOriginalName("doc.pdf");
        file.setOwner(user);
        file = fileEntityRepository.save(file);

        DownloadHistory history = new DownloadHistory();
        history.setDownloadTime(LocalDateTime.now());
        history.setIpAddress("192.168.1.1");
        history.setFile(file);
        history.setUser(user);
        DownloadHistory saved = downloadHistoryRepository.save(history);

        Optional<DownloadHistory> found = downloadHistoryRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(found.get().getFile().getId()).isEqualTo(file.getId());
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
    }
}