/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.entity;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author NiTeFox
 */

class FolderTest {

    @Test
    void testGettersAndSetters() {
        Folder folder = new Folder();
        folder.setId(10L);
        folder.setName("Documents");
        User owner = new User();
        folder.setOwner(owner);

        Folder parent = new Folder();
        folder.setParent(parent);

        Folder child = new Folder();
        folder.setChildren(Set.of(child));

        FileEntity file = new FileEntity();
        folder.setFiles(Set.of(file));

        assertThat(folder.getId()).isEqualTo(10L);
        assertThat(folder.getName()).isEqualTo("Documents");
        assertThat(folder.getOwner()).isEqualTo(owner);
        assertThat(folder.getParent()).isEqualTo(parent);
        assertThat(folder.getChildren()).containsExactly(child);
        assertThat(folder.getFiles()).containsExactly(file);
        assertThat(folder.getCreatedAt()).isNotNull();
    }

    @Test
    void testDefaultCollectionsAreInitialized() {
        Folder folder = new Folder();
        assertThat(folder.getChildren()).isNotNull();
        assertThat(folder.getFiles()).isNotNull();
    }
}