/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.controller;

import com.nitefox.cloudattic.entity.Folder;
import org.springframework.ui.Model;
import com.nitefox.cloudattic.entity.User;
import com.nitefox.cloudattic.repository.FolderRepository;
import com.nitefox.cloudattic.repository.UserRepository;
import com.nitefox.cloudattic.service.FileService;
import com.nitefox.cloudattic.service.FolderService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
@Controller
@RequiredArgsConstructor
public class DiskController {

    private final FileService fileService;
    private final UserRepository userRepository;
    private final FolderService folderService;
    private final FolderRepository folderRepository;
    
    @GetMapping("/disk")
    public String disk(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        log.info("User {} accessed disk page", user.getUsername());
        model.addAttribute("files", fileService.getUserFiles(user));
        model.addAttribute("folders", folderService.getRootFolders(user));
        return "disk";
    }
    
    @PostMapping("/disk/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(required = false) Long folderId,
                         Authentication authentication) throws IOException {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        log.info("Upload request: user={}, file={}, folderId={}", user.getUsername(), file.getOriginalFilename(), folderId);
        fileService.uploadFile(file, user, folderId);
        if (folderId != null) {
            return "redirect:/folder/" + folderId;
        }
        return "redirect:/disk";
    }
    
    @GetMapping("/disk/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id, HttpServletRequest request) throws IOException {
        log.info("Download request from disk for fileId={}", id);
        return fileService.downloadFile(id, request);
    }
    
    @GetMapping("/disk/open/{id}")
    public ResponseEntity<Resource> open(@PathVariable Long id, HttpServletRequest request) throws IOException {
        log.info("Open request from disk for fileId={}", id);
        return fileService.openFile(id, request);
    }
    
    @PostMapping("/api/file/share/{id}")
    @ResponseBody
    public String share(@PathVariable Long id) {
        log.info("Generate public link for fileId={} via API", id);
        return fileService.generatePublicLink(id);
    }
    
    @PostMapping("/folder/create")
    public String createFolder(@RequestParam String name,
                               @RequestParam(required = false) Long parentId,
                               Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        log.info("Create folder '{}' for user={}, parentId={}", name, user.getUsername(), parentId);
        folderService.createFolder(name, user, parentId);
        if (parentId != null) {
            return "redirect:/folder/" + parentId;
        }
        return "redirect:/disk";
    }
    
    @GetMapping("/folder/{id}")
    public String openFolder(@PathVariable Long id, Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        log.info("User {} opened folder id={}", user.getUsername(), id);
        Folder folder = folderService.getFolder(id);
        model.addAttribute("currentFolder", folder);
        model.addAttribute("files", folder.getFiles());
        model.addAttribute("folders", folder.getChildren());
        return "disk";
    }
    
    @PostMapping("/folder/rename/{id}")
    @ResponseBody
    public void renameFolder(@PathVariable Long id, @RequestParam String name) {
        log.info("Rename folder id={} to '{}'", id, name);
        folderService.renameFolder(id, name);
    }
    
    @PostMapping("/folder/delete/{id}")
    @ResponseBody
    public void deleteFolder(@PathVariable Long id) {
        log.warn("Delete folder id={}", id);
        folderService.deleteFolder(id);
    }
    
    @GetMapping("/folder/download/{id}")
    public ResponseEntity<Resource> downloadFolder(@PathVariable Long id) throws IOException {
        log.info("Download folder id={} as ZIP", id);
        return folderService.downloadFolderAsZip(id);
    }
    
    @PostMapping("/disk/upload-folder")
    public String uploadFolder(@RequestParam("files") List<MultipartFile> files,
                               @RequestParam(required = false) Long folderId,
                               Authentication authentication) throws IOException {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        log.info("Upload folder structure: {} files, user={}, folderId={}", files.size(), user.getUsername(), folderId);
        fileService.uploadFolder(files, user, folderId);
        if (folderId != null) {
            return "redirect:/folder/" + folderId;
        }
        return "redirect:/disk";
    }
    
    @GetMapping("/public/download/{token}")
    public ResponseEntity<Resource> downloadPublic(@PathVariable String token, HttpServletRequest request) throws IOException {
        log.info("Public download with token={}", token);
        return fileService.openPublicFile(token, request);
    }
}