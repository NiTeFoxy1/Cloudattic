/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.controller;

import com.nitefox.cloudattic.config.TestSecurityConfig;
import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.ShareLink;
import com.nitefox.cloudattic.service.ShareLinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import org.springframework.context.annotation.Import;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author NiTeFox
 */
// PublicControllerTest.java

@WebMvcTest(PublicController.class)
@Import(TestSecurityConfig.class)
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShareLinkService shareLinkService;

    @Test
    void publicFile_shouldReturnPublicFileView() throws Exception {
        ShareLink link = new ShareLink();
        FileEntity file = new FileEntity();
        file.setId(1L);
        file.setOriginalName("test.txt");
        link.setFile(file);
        link.setToken("abc123");
        when(shareLinkService.findByToken("abc123")).thenReturn(link);

        mockMvc.perform(get("/public/abc123"))
                .andExpect(status().isOk())
                .andExpect(view().name("public-file"))
                .andExpect(model().attributeExists("file", "token"))
                .andExpect(model().attribute("file", file))
                .andExpect(model().attribute("token", "abc123"));
    }
}