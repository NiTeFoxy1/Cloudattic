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


class FileTagTest {

    @Test
    void testNoArgsConstructor() {
        FileTag tag = new FileTag();
        tag.setName("test");
        assertThat(tag.getName()).isEqualTo("test");
        // id остаётся null, так как сеттера нет
        assertThat(tag.getId()).isNull();
    }

    @Test
    void testConstructorWithName() {
        FileTag tag = new FileTag("confidential");
        assertThat(tag.getName()).isEqualTo("confidential");
        assertThat(tag.getFiles()).isEmpty();
        assertThat(tag.getId()).isNull();
    }

    @Test
    void testGettersAndSetters() {
        FileTag tag = new FileTag();
        tag.setName("urgent");
        FileEntity file = new FileEntity();
        tag.setFiles(Set.of(file));

        assertThat(tag.getName()).isEqualTo("urgent");
        assertThat(tag.getFiles()).containsExactly(file);
        // id не тестируем, так как нет сеттера
    }

    @Test
    void testEqualsAndHashCodeBasedOnName() {
        FileTag tag1 = new FileTag("tagA");
        FileTag tag2 = new FileTag("tagA");
        FileTag tag3 = new FileTag("tagB");

        assertThat(tag1).isEqualTo(tag2);
        assertThat(tag1).isNotEqualTo(tag3);
        assertThat(tag1.hashCode()).isEqualTo(tag2.hashCode());
    }
}