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
import com.nitefox.cloudattic.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, Long> {
    
    List<FileEntity> findByOwner(User owner);
    List<FileEntity> findByOwnerAndFolderIsNull(User owner);
}