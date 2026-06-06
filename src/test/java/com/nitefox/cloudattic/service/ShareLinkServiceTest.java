/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.service;

import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.ShareLink;
import com.nitefox.cloudattic.repository.ShareLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 *
 * @author NiTeFox
 */


@ExtendWith(MockitoExtension.class)
class ShareLinkServiceTest {

    @Mock
    private ShareLinkRepository shareLinkRepository;

    @InjectMocks
    private ShareLinkService shareLinkService;

    private FileEntity testFile;
    private ShareLink validLink;
    private ShareLink expiredLink;

    @BeforeEach
    void setUp() {
        testFile = new FileEntity();
        testFile.setId(1L);

        validLink = new ShareLink();
        validLink.setToken("valid-token");
        validLink.setFile(testFile);
        validLink.setExpirationDate(LocalDateTime.now().plusHours(1));

        expiredLink = new ShareLink();
        expiredLink.setToken("expired-token");
        expiredLink.setFile(testFile);
        expiredLink.setExpirationDate(LocalDateTime.now().minusHours(1));
    }

    @Test
    void findByToken_ShouldReturnLink_WhenValid() {
        when(shareLinkRepository.findByToken("valid-token")).thenReturn(Optional.of(validLink));

        ShareLink found = shareLinkService.findByToken("valid-token");
        assertThat(found).isEqualTo(validLink);
    }

    @Test
    void findByToken_ShouldThrowNotFound_WhenTokenMissing() {
        when(shareLinkRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shareLinkService.findByToken("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findByToken_ShouldThrowForbidden_WhenExpired() {
        when(shareLinkRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredLink));

        assertThatThrownBy(() -> shareLinkService.findByToken("expired-token"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createLink_ShouldSaveWithTokenAndExpiration() {
        when(shareLinkRepository.save(any(ShareLink.class))).thenAnswer(inv -> inv.getArgument(0));

        ShareLink created = shareLinkService.createLink(testFile, 24);

        assertThat(created.getFile()).isEqualTo(testFile);
        assertThat(created.getToken()).isNotNull();
        assertThat(created.isPublicAccess()).isTrue();
        assertThat(created.getExpirationDate()).isAfter(LocalDateTime.now().plusHours(23));
        verify(shareLinkRepository).save(created);
    }

    @Test
    void cleanupExpiredLinks_ShouldDeleteExpiredLinks() {
        when(shareLinkRepository.findAll()).thenReturn(List.of(validLink, expiredLink));

        shareLinkService.cleanupExpiredLinks();

        verify(shareLinkRepository).delete(expiredLink);
        verify(shareLinkRepository, never()).delete(validLink);
    }
}