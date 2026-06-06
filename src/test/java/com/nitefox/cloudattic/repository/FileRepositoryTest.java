/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.repository;

import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.Folder;
import com.nitefox.cloudattic.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
/**
 *
 * @author NiTeFox
 */


@DataJpaTest
class FileEntityRepositoryTest {

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FolderRepository folderRepository;

    private User owner;
    private Folder folder;
    private FileEntity fileInRoot;
    private FileEntity fileInFolder;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setUsername("owner");
        owner.setEmail("owner@example.com");
        owner.setPassword("pass");
        owner = userRepository.save(owner);

        folder = new Folder();
        folder.setName("Subfolder");
        folder.setOwner(owner);
        folder = folderRepository.save(folder);

        fileInRoot = new FileEntity();
        fileInRoot.setOriginalName("root.txt");
        fileInRoot.setOwner(owner);
        fileInRoot.setFolder(null);
        fileInRoot = fileEntityRepository.save(fileInRoot);

        fileInFolder = new FileEntity();
        fileInFolder.setOriginalName("inside.txt");
        fileInFolder.setOwner(owner);
        fileInFolder.setFolder(folder);
        fileInFolder = fileEntityRepository.save(fileInFolder);
    }

    @Test
    void findByOwner_ShouldReturnAllUserFiles() {
        List<FileEntity> files = fileEntityRepository.findByOwner(owner);
        assertThat(files).hasSize(2);
        assertThat(files).extracting(FileEntity::getOriginalName)
                .containsExactlyInAnyOrder("root.txt", "inside.txt");
    }

    @Test
    void findByOwnerAndFolderIsNull_ShouldReturnOnlyRootFiles() {
        List<FileEntity> rootFiles = fileEntityRepository.findByOwnerAndFolderIsNull(owner);
        assertThat(rootFiles).hasSize(1);
        assertThat(rootFiles.get(0).getOriginalName()).isEqualTo("root.txt");
    }
}