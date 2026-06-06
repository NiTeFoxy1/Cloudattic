/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.repository;

import com.nitefox.cloudattic.entity.Folder;
import com.nitefox.cloudattic.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author NiTeFox
 */

@DataJpaTest
class FolderRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Folder rootFolder;
    private Folder childFolder;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setUsername("owner");
        owner.setEmail("owner@ex.com");
        owner.setPassword("pwd");
        owner = userRepository.save(owner);

        rootFolder = new Folder();
        rootFolder.setName("Root");
        rootFolder.setOwner(owner);
        rootFolder.setParent(null);
        rootFolder = folderRepository.save(rootFolder);

        childFolder = new Folder();
        childFolder.setName("Child");
        childFolder.setOwner(owner);
        childFolder.setParent(rootFolder);
        childFolder = folderRepository.save(childFolder);
    }

    @Test
    void findByOwner_ShouldReturnAllFoldersOfUser() {
        List<Folder> folders = folderRepository.findByOwner(owner);
        assertThat(folders).hasSize(2);
        assertThat(folders).extracting(Folder::getName)
                .containsExactlyInAnyOrder("Root", "Child");
    }

    @Test
    void findByOwnerAndParentIsNull_ShouldReturnRootFolders() {
        List<Folder> rootFolders = folderRepository.findByOwnerAndParentIsNull(owner);
        assertThat(rootFolders).hasSize(1);
        assertThat(rootFolders.get(0).getName()).isEqualTo("Root");
    }

    @Test
    void findByNameAndParent_ShouldReturnFolder_WhenExists() {
        Optional<Folder> found = folderRepository.findByNameAndParent("Child", rootFolder);
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(childFolder.getId());
    }

    @Test
    void findByNameAndParent_ShouldReturnEmpty_WhenNotExists() {
        Optional<Folder> found = folderRepository.findByNameAndParent("NotExists", rootFolder);
        assertThat(found).isEmpty();
    }
}