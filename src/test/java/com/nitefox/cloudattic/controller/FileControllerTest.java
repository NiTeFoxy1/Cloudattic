package com.nitefox.cloudattic.controller;

import com.nitefox.cloudattic.config.TestSecurityConfig;
import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.User;
import com.nitefox.cloudattic.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@Import(TestSecurityConfig.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        // Не вызываем getAuthorities() – передаём null для authorities
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(testUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void upload_shouldReturnFileEntity() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(1L);
        when(fileService.uploadFile(any(), any(), any())).thenReturn(fileEntity);

        mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .param("folderId", "10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void download_shouldReturnResource() throws Exception {
        Resource resource = new ByteArrayResource("data".getBytes());
        when(fileService.downloadFile(eq(1L), any()))
                .thenReturn(ResponseEntity.ok().body(resource));

        mockMvc.perform(get("/files/download/1"))
                .andExpect(status().isOk());
    }

    @Test
    void downloadPublicFile_shouldReturnResource() throws Exception {
        Resource resource = new ByteArrayResource("data".getBytes());
        when(fileService.downloadPublicFile(eq("token123"), any()))
                .thenReturn(ResponseEntity.ok().body(resource));

        mockMvc.perform(get("/files/public/download/token123"))
                .andExpect(status().isOk());
    }

    @Test
    void open_shouldReturnResource() throws Exception {
        Resource resource = new ByteArrayResource("data".getBytes());
        when(fileService.openFile(eq(1L), any()))
                .thenReturn(ResponseEntity.ok().body(resource));

        mockMvc.perform(get("/files/open/1"))
                .andExpect(status().isOk());
    }

    @Test
    void generatePublic_shouldReturnToken() throws Exception {
        when(fileService.generatePublicLink(1L)).thenReturn("public-token");

        mockMvc.perform(post("/files/public/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("public-token"));
    }

    @Test
    void openPublic_shouldReturnResource() throws Exception {
        Resource resource = new ByteArrayResource("data".getBytes());
        when(fileService.openPublicFile(eq("token123"), any()))
                .thenReturn(ResponseEntity.ok().body(resource));

        mockMvc.perform(get("/files/public/token123"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFile_shouldReturnOk() throws Exception {
        doNothing().when(fileService).deleteFile(1L);

        mockMvc.perform(post("/files/disk/delete/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void renameFile_shouldReturnOk() throws Exception {
        doNothing().when(fileService).renameFile(1L, "newName");

        mockMvc.perform(post("/files/disk/rename/1")
                        .param("name", "newName")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}