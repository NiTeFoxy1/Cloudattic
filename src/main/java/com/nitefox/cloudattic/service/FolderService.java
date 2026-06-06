/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.service;

import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.Folder;
import com.nitefox.cloudattic.entity.User;
import com.nitefox.cloudattic.repository.FolderRepository;
import org.springframework.http.ContentDisposition;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
/**
 *
 * @author NiTeFox
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    public Folder createFolder(String name, User user, Long parentId) {
        log.info("Creating folder '{}' for user {}, parentId={}", name, user.getUsername(), parentId);
        Folder folder = new Folder();
        folder.setName(name);
        folder.setOwner(user);
        if (parentId != null) {
            Folder parent = folderRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
            if (!parent.getOwner().getId().equals(user.getId())) {
                log.warn("User {} attempted to create folder in unauthorized parent folder {}", user.getId(), parentId);
                throw new RuntimeException("No access to parent folder");
            }
            folder.setParent(parent);
        }
        Folder saved = folderRepository.save(folder);
        log.info("Folder created with id={}", saved.getId());
        return saved;
    }
    
    public List<Folder> getUserFolders(User user) {
        log.debug("Fetching folders for user id={}", user.getId());
        return folderRepository.findByOwner(user);
    }

    public void renameFolder(Long id, String name) {
        log.info("Renaming folder id={} to '{}'", id, name);
        Folder folder = folderRepository.findById(id).orElseThrow();
        folder.setName(name);
        folderRepository.save(folder);
    }

    public void deleteFolder(Long id) {
        log.warn("Deleting folder id={}", id);
        Folder folder = folderRepository.findById(id).orElseThrow();
        folderRepository.delete(folder);
        log.info("Folder id={} deleted", id);
    }

    public Folder getFolder(Long id) {
        log.debug("Fetching folder id={}", id);
        return folderRepository.findById(id).orElseThrow();
    }
    
    public List<Folder> getRootFolders(User user) {
        log.debug("Fetching root folders for user id={}", user.getId());
        return folderRepository.findByOwnerAndParentIsNull(user);
    }
    
    public ResponseEntity<Resource> downloadFolderAsZip(Long folderId) throws IOException {
        log.info("Downloading folder id={} as ZIP", folderId);
        Folder folder = folderRepository.findById(folderId).orElseThrow();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        zipFolder(folder, folder.getName(), zos);
        zos.close();
        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(folder.getName() + ".zip", StandardCharsets.UTF_8)
                .build();
        log.info("ZIP archive created for folder {}, size={} bytes", folderId, resource.contentLength());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }
    
    private void zipFolder(Folder folder, String path, ZipOutputStream zos) throws IOException {
        log.debug("Zipping folder: {} -> {}", folder.getName(), path);
        for (FileEntity file : folder.getFiles()) {
            ZipEntry entry = new ZipEntry(path + "/" + file.getOriginalName());
            zos.putNextEntry(entry);
            Files.copy(Paths.get(file.getPath()), zos);
            zos.closeEntry();
            log.debug("Added file {} to ZIP", file.getOriginalName());
        }
        for (Folder child : folder.getChildren()) {
            zipFolder(child, path + "/" + child.getName(), zos);
        }
    }
}