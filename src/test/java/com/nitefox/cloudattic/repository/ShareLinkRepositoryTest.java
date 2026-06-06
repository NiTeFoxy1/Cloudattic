/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.repository;

import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.ShareLink;
import com.nitefox.cloudattic.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
/**
 *
 * @author NiTeFox
 */


@DataJpaTest
class ShareLinkRepositoryTest {

    @Autowired
    private ShareLinkRepository shareLinkRepository;

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Autowired
    private UserRepository userRepository;

    private FileEntity file;
    private ShareLink link;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setUsername("owner");
        owner.setEmail("owner@ex.com");
        owner.setPassword("pwd");
        owner = userRepository.save(owner);

        file = new FileEntity();
        file.setOriginalName("secret.txt");
        file.setOwner(owner);
        file = fileEntityRepository.save(file);

        link = new ShareLink();
        link.setToken("abc123");
        link.setExpirationDate(LocalDateTime.now().plusDays(1));
        link.setPublicAccess(true);
        link.setFile(file);
        link = shareLinkRepository.save(link);
    }

    @Test
    void findByToken_ShouldReturnLink_WhenTokenExists() {
        Optional<ShareLink> found = shareLinkRepository.findByToken("abc123");
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(link.getId());
    }

    @Test
    void findByToken_ShouldReturnEmpty_WhenTokenNotExists() {
        Optional<ShareLink> found = shareLinkRepository.findByToken("nonexistent");
        assertThat(found).isEmpty();
    }

    @Test
    void findAllByFile_ShouldReturnAllLinksForFile() {
        List<ShareLink> links = shareLinkRepository.findAllByFile(file);
        assertThat(links).hasSize(1);
        assertThat(links.get(0).getToken()).isEqualTo("abc123");
    }
}