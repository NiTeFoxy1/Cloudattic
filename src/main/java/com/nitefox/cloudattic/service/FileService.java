/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.service;

import com.nitefox.cloudattic.entity.DownloadHistory;
import com.nitefox.cloudattic.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.Folder;
import com.nitefox.cloudattic.entity.ShareLink;
import com.nitefox.cloudattic.repository.DownloadHistoryRepository;
import com.nitefox.cloudattic.repository.FileEntityRepository;
import com.nitefox.cloudattic.repository.FolderRepository;
import com.nitefox.cloudattic.repository.ShareLinkRepository;
import com.nitefox.cloudattic.repository.UserRepository;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;
import org.springframework.http.MediaType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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
@RequiredArgsConstructor
public class FileService {

    private final FileEntityRepository fileRepository;
    private final UserRepository userRepository;
    private final String uploadDir = "uploads/";
    private final FolderRepository folderRepository;
    private final ShareLinkRepository shareLinkRepository;
    private final ShareLinkService shareLinkService;
    private final DownloadHistoryRepository downloadHistoryRepository;
    
    private void logDownload(FileEntity file, User user, String ip) {
        log.info("Download logged: fileId={}, userId={}, ip={}", file.getId(), user != null ? user.getId() : null, ip);
        DownloadHistory history = new DownloadHistory();
        history.setFile(file);
        history.setUser(user);
        history.setIpAddress(ip);
        downloadHistoryRepository.save(history);
    }
    
    public List<FileEntity> getUserFiles(User user) {
        log.debug("Fetching files for user id={}", user.getId());
        return fileRepository.findByOwnerAndFolderIsNull(user);
    }
    
    public ResponseEntity<Resource> download(@PathVariable Long id) throws MalformedURLException {
        log.info("Download request for file id={}", id);
        FileEntity file = fileRepository.findById(id).orElseThrow();
        Path path = Paths.get("uploads").resolve(file.getStoredName());
        Resource resource = new UrlResource(path.toUri());
        String encodedFileName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
            .body(resource);
    }
    
    public FileEntity uploadFile(MultipartFile file, User user, Long folderId) throws IOException {
        log.info("Uploading file: originalName={}, user={}, folderId={}", file.getOriginalFilename(), user.getUsername(), folderId);
        String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + storedName);
        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path);
        FileEntity entity = new FileEntity();
        entity.setOriginalName(file.getOriginalFilename());
        entity.setStoredName(storedName);
        entity.setMimeType(file.getContentType());
        entity.setSize(file.getSize());
        entity.setPath(path.toString());
        entity.setUploadDate(LocalDateTime.now());
        entity.setOwner(user);
        if (folderId != null) {
            Folder folder = folderRepository.findById(folderId).orElseThrow();
            entity.setFolder(folder);
        }
        FileEntity saved = fileRepository.save(entity);
        log.info("File uploaded successfully: id={}, storedName={}", saved.getId(), storedName);
        return saved;
    }
    
    public ResponseEntity<Resource> downloadFile(Long id, HttpServletRequest request) throws IOException {
        log.info("Download file id={}, remoteAddr={}", id, request.getRemoteAddr());
        FileEntity file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        logDownload(file, null, ip);
        Path path = Paths.get(file.getPath());
        Resource resource = new UrlResource(path.toUri());
        String encodedFileName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .body(resource);
    }
    
    public ResponseEntity<Resource> downloadPublicFile(String token, HttpServletRequest request) throws IOException {
        log.info("Download public file with token={}, ip={}", token, request.getRemoteAddr());
        ShareLink link = shareLinkRepository.findByToken(token).orElseThrow();
        FileEntity file = link.getFile();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        logDownload(file, null, ip);
        Path path = Paths.get(file.getPath());
        Resource resource = new UrlResource(path.toUri());
        String encodedFileName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .body(resource);
    }
    
    public ResponseEntity<Resource> openFile(Long id, HttpServletRequest request) throws IOException {
        log.info("Open file id={} (inline), ip={}", id, request.getRemoteAddr());
        FileEntity file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        logDownload(file, null, ip);
        Path path = Paths.get(file.getPath());
        Resource resource = new UrlResource(path.toUri());
        String encodedFileName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .body(resource);
    }
    
    public String generatePublicLink(Long fileId) {
        log.info("Generating public link for fileId={}", fileId);
        FileEntity file = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        ShareLink link = shareLinkService.createLink(file, 24);
        String url = "http://localhost:8080/public/" + link.getToken();
        log.info("Public link generated: {}", url);
        return url;
    }
    
    public ResponseEntity<Resource> openPublicFile(String token, HttpServletRequest request) throws IOException {
        log.info("Open public file with token={}, ip={}", token, request.getRemoteAddr());
        ShareLink link = shareLinkRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Link not found"));
        FileEntity file = link.getFile();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        logDownload(file, null, ip);
        Path path = Paths.get(file.getPath());
        Resource resource = new UrlResource(path.toUri());
        String encodedFileName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .body(resource);
    }
    
    public void renameFile(Long id, String newName) {
        log.info("Renaming file id={} to '{}'", id, newName);
        FileEntity file = fileRepository.findById(id).orElseThrow();
        file.setOriginalName(newName);
        fileRepository.save(file);
        log.debug("File renamed successfully");
    }
    
    @Transactional
    public void deleteFile(Long id) throws IOException {
        log.warn("Deleting file id={}", id);
        FileEntity file = fileRepository.findById(id).orElseThrow();

        // 1. Удаляем связанные публичные ссылки
        List<ShareLink> links = shareLinkRepository.findAllByFile(file);
        if (!links.isEmpty()) {
            log.debug("Deleting {} associated share links", links.size());
            shareLinkRepository.deleteAll(links);
        }

        // 2. Удаляем историю скачиваний
        downloadHistoryRepository.deleteByFile(file);
        log.debug("Deleted download history for file id={}", id);

        // 3. Удаляем физический файл
        Files.deleteIfExists(Paths.get(file.getPath()));

        // 4. Удаляем запись из БД
        fileRepository.delete(file);
        log.info("File id={} deleted", id);
    }
    
    public void uploadFolder(List<MultipartFile> files, User user, Long folderId) throws IOException {
        log.info("Uploading folder structure: {} files, user={}, folderId={}", files.size(), user.getUsername(), folderId);
        Map<String, Folder> folderCache = new HashMap<>();
        Folder root = null;
        if (folderId != null) {
            root = folderRepository.findById(folderId).orElseThrow();
        }
        for (MultipartFile file : files) {
            String fullPath = file.getOriginalFilename();
            if (fullPath == null) continue;
            log.debug("Processing folder file: path={}", fullPath);
            String[] parts = fullPath.split("[/\\\\]");
            Folder parent = root;
            for (int i = 0; i < parts.length - 1; i++) {
                String folderName = parts[i];
                String key = (parent != null ? parent.getId() : "root") + "/" + folderName;
                Folder folder = folderCache.get(key);
                if (folder == null) {
                    folder = new Folder();
                    folder.setName(folderName);
                    folder.setOwner(user);
                    folder.setParent(parent);
                    folder = folderRepository.save(folder);
                    folderCache.put(key, folder);
                    log.debug("Created folder: {} (parent={})", folderName, parent != null ? parent.getId() : "root");
                }
                parent = folder;
            }
            String fileName = parts[parts.length - 1];
            String storedName = UUID.randomUUID().toString();
            Path uploadDir = Paths.get("uploads");
            if (parent != null) {
                uploadDir = uploadDir.resolve(String.valueOf(parent.getId()));
            }
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            FileEntity entity = new FileEntity();
            entity.setOriginalName(fileName);
            entity.setStoredName(storedName);
            entity.setSize(file.getSize());
            entity.setMimeType(file.getContentType());
            entity.setOwner(user);
            entity.setFolder(parent);
            entity.setPath(target.toString());
            fileRepository.save(entity);
            log.debug("Uploaded file: {} -> {}", fileName, target);
        }
        log.info("Folder upload completed for user {}", user.getUsername());
    }
    
    private String encodeFilename(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8);
    }
}