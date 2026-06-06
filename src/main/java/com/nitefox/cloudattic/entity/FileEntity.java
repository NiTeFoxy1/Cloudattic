/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author NiTeFox
 */
package com.nitefox.cloudattic.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalName;

    private String storedName;

    private Long size;

    private String mimeType;

    private LocalDateTime uploadDate = LocalDateTime.now();
    
    // private boolean publicAccess;
    
   // private String publicToken;
    
    private String path;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @OneToMany(mappedBy = "file")
    private Set<ShareLink> shareLinks = new HashSet<>();

    @OneToMany(mappedBy = "file")
    private Set<DownloadHistory> downloads = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "file_tag_relations",
        joinColumns = @JoinColumn(name = "file_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<FileTag> tags = new HashSet<>();
}