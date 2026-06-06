/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;


/**
 *
 * @author NiTeFox
 */

class FileEntityTest {

    @Test
    void testGettersAndSetters() {
        FileEntity file = new FileEntity();
        file.setId(10L);
        file.setOriginalName("report.pdf");
        file.setStoredName("uuid-report.pdf");
        file.setSize(2048L);
        file.setMimeType("application/pdf");
        file.setUploadDate(LocalDateTime.of(2025, 3, 20, 10, 0));
        file.setPath("/user-files/10/report.pdf");

        User owner = new User();
        file.setOwner(owner);

        Folder folder = new Folder();
        file.setFolder(folder);

        ShareLink link = new ShareLink();
        file.setShareLinks(Set.of(link));

        DownloadHistory history = new DownloadHistory();
        file.setDownloads(Set.of(history));

        FileTag tag = new FileTag("important");
        file.setTags(Set.of(tag));

        assertThat(file.getId()).isEqualTo(10L);
        assertThat(file.getOriginalName()).isEqualTo("report.pdf");
        assertThat(file.getStoredName()).isEqualTo("uuid-report.pdf");
        assertThat(file.getSize()).isEqualTo(2048L);
        assertThat(file.getMimeType()).isEqualTo("application/pdf");
        assertThat(file.getUploadDate()).isEqualTo("2025-03-20T10:00");
        assertThat(file.getPath()).isEqualTo("/user-files/10/report.pdf");
        assertThat(file.getOwner()).isEqualTo(owner);
        assertThat(file.getFolder()).isEqualTo(folder);
        assertThat(file.getShareLinks()).containsExactly(link);
        assertThat(file.getDownloads()).containsExactly(history);
        assertThat(file.getTags()).containsExactly(tag);
    }

    @Test
    void testDefaultCollectionsAreInitialized() {
        FileEntity file = new FileEntity();
        assertThat(file.getShareLinks()).isNotNull();
        assertThat(file.getDownloads()).isNotNull();
        assertThat(file.getTags()).isNotNull();
    }
}