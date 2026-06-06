/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.service;

import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.ShareLink;
import com.nitefox.cloudattic.repository.ShareLinkRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 *
 * @author NiTeFox
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class ShareLinkService {

    private final ShareLinkRepository shareLinkRepository;

    public ShareLink findByToken(String token) {
        log.debug("Looking up share link by token: {}", token);
        ShareLink link = shareLinkRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Share link not found: {}", token);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                });
        if (link.getExpirationDate() != null && link.getExpirationDate().isBefore(LocalDateTime.now())) {
            log.info("Share link {} has expired (expired at {})", token, link.getExpirationDate());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "EXPIRED");
        }
        return link;
    }
    
    public ShareLink createLink(FileEntity file, long hoursToLive) {
        log.info("Creating share link for file id={}, hoursToLive={}", file.getId(), hoursToLive);
        ShareLink link = new ShareLink();
        link.setFile(file);
        link.setToken(UUID.randomUUID().toString());
        link.setPublicAccess(true);
        link.setExpirationDate(LocalDateTime.now().plusHours(hoursToLive));
        ShareLink saved = shareLinkRepository.save(link);
        log.info("Share link created: token={}, expires={}", saved.getToken(), saved.getExpirationDate());
        return saved;
    }
    
    @Scheduled(fixedRate = 3600000) // каждый час
    public void cleanupExpiredLinks() {
        log.debug("Running scheduled cleanup of expired share links");
        List<ShareLink> all = shareLinkRepository.findAll();
        int deleted = 0;
        for (ShareLink link : all) {
            if (link.getExpirationDate() != null && link.getExpirationDate().isBefore(LocalDateTime.now())) {
                shareLinkRepository.delete(link);
                deleted++;
                log.debug("Deleted expired link: token={}, expiredAt={}", link.getToken(), link.getExpirationDate());
            }
        }
        if (deleted > 0) {
            log.info("Cleaned up {} expired share links", deleted);
        }
    }
}