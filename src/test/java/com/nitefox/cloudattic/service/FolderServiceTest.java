/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.service;

import com.nitefox.cloudattic.entity.Folder;
import com.nitefox.cloudattic.entity.User;
import com.nitefox.cloudattic.repository.FolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpHeaders;
/**
 *
 * @author NiTeFox
 */


@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderService folderService;

    private User owner;
    private Folder rootFolder;
    private Folder childFolder;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        rootFolder = new Folder();
        rootFolder.setId(10L);
        rootFolder.setName("Root");
        rootFolder.setOwner(owner);
        rootFolder.setParent(null);

        childFolder = new Folder();
        childFolder.setId(20L);
        childFolder.setName("Child");
        childFolder.setOwner(owner);
        childFolder.setParent(rootFolder);
    }

    @Test
    void createFolder_ShouldCreateRootFolder() {
        when(folderRepository.save(any(Folder.class))).thenAnswer(inv -> inv.getArgument(0));

        Folder created = folderService.createFolder("NewRoot", owner, null);

        assertThat(created.getName()).isEqualTo("NewRoot");
        assertThat(created.getOwner()).isEqualTo(owner);
        assertThat(created.getParent()).isNull();
        verify(folderRepository).save(any(Folder.class));
    }

    @Test
    void createFolder_ShouldCreateSubFolder_WhenParentValid() {
        when(folderRepository.findById(10L)).thenReturn(Optional.of(rootFolder));
        when(folderRepository.save(any(Folder.class))).thenAnswer(inv -> inv.getArgument(0));

        Folder created = folderService.createFolder("Sub", owner, 10L);

        assertThat(created.getParent()).isEqualTo(rootFolder);
        verify(folderRepository).findById(10L);
    }

    @Test
    void createFolder_ShouldThrow_WhenParentNotFound() {
        when(folderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> folderService.createFolder("Fail", owner, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Parent folder not found");
    }

    @Test
    void createFolder_ShouldThrow_WhenUserNotOwnerOfParent() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        rootFolder.setOwner(anotherUser);
        when(folderRepository.findById(10L)).thenReturn(Optional.of(rootFolder));

        assertThatThrownBy(() -> folderService.createFolder("Fail", owner, 10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No access to parent folder");
    }

    @Test
    void getUserFolders_ShouldReturnAllUserFolders() {
        when(folderRepository.findByOwner(owner)).thenReturn(List.of(rootFolder, childFolder));

        List<Folder> folders = folderService.getUserFolders(owner);
        assertThat(folders).hasSize(2);
    }

    @Test
    void getRootFolders_ShouldReturnFoldersWithoutParent() {
        when(folderRepository.findByOwnerAndParentIsNull(owner)).thenReturn(List.of(rootFolder));

        List<Folder> roots = folderService.getRootFolders(owner);
        assertThat(roots).containsExactly(rootFolder);
    }

    @Test
    void renameFolder_ShouldUpdateName() {
        when(folderRepository.findById(10L)).thenReturn(Optional.of(rootFolder));
        folderService.renameFolder(10L, "Renamed");

        assertThat(rootFolder.getName()).isEqualTo("Renamed");
        verify(folderRepository).save(rootFolder);
    }

    @Test
    void deleteFolder_ShouldCallRepositoryDelete() {
        when(folderRepository.findById(10L)).thenReturn(Optional.of(rootFolder));
        folderService.deleteFolder(10L);
        verify(folderRepository).delete(rootFolder);
    }

    @Test
    void getFolder_ShouldReturnFolder() {
        when(folderRepository.findById(10L)).thenReturn(Optional.of(rootFolder));
        Folder found = folderService.getFolder(10L);
        assertThat(found).isEqualTo(rootFolder);
    }

    @Test
    void downloadFolderAsZip_ShouldReturnZipResponse() throws IOException {
        when(folderRepository.findById(10L)).thenReturn(Optional.of(rootFolder));
        // Так как zipFolder работает с реальными файлами, для простоты протестируем только что метод выполняется без ошибок
        ResponseEntity<Resource> response = folderService.downloadFolderAsZip(10L);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).contains("attachment");
        assertThat(response.getBody()).isNotNull();
    }
}