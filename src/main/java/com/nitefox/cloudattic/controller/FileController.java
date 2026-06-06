/*
*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.controller;

import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.User;
import com.nitefox.cloudattic.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author NiTeFox
 */
/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    
    @PostMapping("/upload")
    public FileEntity upload(@RequestParam MultipartFile file,
                             @RequestParam(required = false) Long folderId,
                             @AuthenticationPrincipal User user) throws IOException {
        log.info("REST upload: file={}, folderId={}, user={}", file.getOriginalFilename(), folderId, user.getUsername());
        return fileService.uploadFile(file, user, folderId);
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id, HttpServletRequest request) throws IOException {
        log.info("REST download file id={}", id);
        return fileService.downloadFile(id, request);
    }
    
    @GetMapping("/public/download/{token}")
    public ResponseEntity<Resource> downloadPublicFile(@PathVariable String token, HttpServletRequest request) throws IOException {
        log.info("REST public download with token={}", token);
        return fileService.downloadPublicFile(token, request);
    }
    
    @GetMapping("/open/{id}")
    public ResponseEntity<Resource> open(@PathVariable Long id, HttpServletRequest request) throws IOException {
        log.info("REST open file id={}", id);
        return fileService.openFile(id, request);
    }
    
    @PostMapping("/public/{id}")
    public String generatePublic(@PathVariable Long id) {
        log.info("REST generate public link for fileId={}", id);
        return fileService.generatePublicLink(id);
    }
    
    @GetMapping("/public/{token}")
    public ResponseEntity<Resource> openPublic(@PathVariable String token, HttpServletRequest request) throws IOException {
        log.info("REST open public file with token={}", token);
        return fileService.openPublicFile(token, request);
    }
    
    @PostMapping("/disk/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) throws IOException {
        log.warn("REST delete file id={}", id);
        fileService.deleteFile(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/disk/rename/{id}")
    public ResponseEntity<Void> renameFile(@PathVariable Long id, @RequestParam String name) {
        log.info("REST rename file id={} to '{}'", id, name);
        fileService.renameFile(id, name);
        return ResponseEntity.ok().build();
    }
}