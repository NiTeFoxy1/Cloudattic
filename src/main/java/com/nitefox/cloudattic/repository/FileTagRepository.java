/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.repository;

/**
 *
 * @author NiTeFox
 */
import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.FileTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileTagRepository extends JpaRepository<FileTag, Long> {
}

