/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.controller;

import com.nitefox.cloudattic.config.TestSecurityConfig;
import com.nitefox.cloudattic.entity.Folder;
import com.nitefox.cloudattic.entity.User;
import com.nitefox.cloudattic.repository.FolderRepository;
import com.nitefox.cloudattic.repository.UserRepository;
import com.nitefox.cloudattic.service.FileService;
import com.nitefox.cloudattic.service.FolderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author NiTeFox
 */

@WebMvcTest(DiskController.class)
@Import(TestSecurityConfig.class)
class DiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private FolderService folderService;

    @MockitoBean
    private FolderRepository folderRepository;

    @Test
    @WithMockUser(username = "user@example.com")
    void disk_shouldReturnDiskViewWithFilesAndFolders() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(fileService.getUserFiles(user)).thenReturn(List.of());
        when(folderService.getRootFolders(user)).thenReturn(List.of());

        mockMvc.perform(get("/disk"))
                .andExpect(status().isOk())
                .andExpect(view().name("disk"))
                .andExpect(model().attributeExists("files", "folders"));
    }

    @Test
    @WithMockUser
    void upload_shouldRedirectToDiskOrFolder() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());
        User user = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(multipart("/disk/upload")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/disk"));

        mockMvc.perform(multipart("/disk/upload")
                        .file(file)
                        .param("folderId", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/folder/5"));
    }

    @Test
    @WithMockUser
    void download_shouldReturnResource() throws Exception {
        Resource resource = new ByteArrayResource("data".getBytes());
        when(fileService.downloadFile(eq(1L), any()))
                .thenReturn(ResponseEntity.ok().body(resource));

        mockMvc.perform(get("/disk/download/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void open_shouldReturnResource() throws Exception {
        Resource resource = new ByteArrayResource("data".getBytes());
        when(fileService.openFile(eq(1L), any()))
                .thenReturn(ResponseEntity.ok().body(resource));

        mockMvc.perform(get("/disk/open/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void share_shouldReturnToken() throws Exception {
        when(fileService.generatePublicLink(1L)).thenReturn("share-token");

        mockMvc.perform(post("/api/file/share/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("share-token"));
    }

    @Test
    @WithMockUser
    void createFolder_shouldRedirectToDiskOrParent() throws Exception {
        User user = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/folder/create")
                        .param("name", "NewFolder")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/disk"));

        mockMvc.perform(post("/folder/create")
                        .param("name", "NewFolder")
                        .param("parentId", "10")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/folder/10"));
    }

    @Test
    @WithMockUser
    void openFolder_shouldReturnDiskViewWithFolderData() throws Exception {
        User user = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        Folder folder = new Folder();
        folder.setId(5L);
        when(folderService.getFolder(5L)).thenReturn(folder);

        mockMvc.perform(get("/folder/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("disk"))
                .andExpect(model().attribute("currentFolder", folder))
                .andExpect(model().attributeExists("files", "folders"));
    }

    @Test
    @WithMockUser
    void renameFolder_shouldReturnOk() throws Exception {
        doNothing().when(folderService).renameFolder(1L, "newName");

        mockMvc.perform(post("/folder/rename/1")
                        .param("name", "newName")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteFolder_shouldReturnOk() throws Exception {
        doNothing().when(folderService).deleteFolder(1L);

        mockMvc.perform(post("/folder/delete/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void downloadFolder_shouldReturnZipResource() throws Exception {
        Resource zip = new ByteArrayResource("zip".getBytes());
        when(folderService.downloadFolderAsZip(1L))
                .thenReturn(ResponseEntity.ok().body(zip));

        mockMvc.perform(get("/folder/download/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void uploadFolder_shouldRedirect() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "a.txt", "text/plain", "data".getBytes());
        User user = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(multipart("/disk/upload-folder")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/disk"));
    }

    @Test
    void downloadPublic_shouldReturnResource() throws Exception {
        Resource resource = new ByteArrayResource("data".getBytes());
        when(fileService.openPublicFile(eq("tok"), any()))
                .thenReturn(ResponseEntity.ok().body(resource));

        mockMvc.perform(get("/public/download/tok"))
                .andExpect(status().isOk());
    }
}