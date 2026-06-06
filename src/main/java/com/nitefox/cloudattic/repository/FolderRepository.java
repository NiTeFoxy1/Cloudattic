/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.repository;

/**
 *
 * @author NiTeFox
 */
import com.nitefox.cloudattic.entity.Folder;
import com.nitefox.cloudattic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByOwner(User owner);
    List<Folder> findByOwnerAndParentIsNull(User owner);
    Optional<Folder> findByNameAndParent(
        String name,
        Folder parent
    );
}