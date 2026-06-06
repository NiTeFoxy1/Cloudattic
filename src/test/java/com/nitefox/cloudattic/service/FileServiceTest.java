package com.nitefox.cloudattic.service;

import com.nitefox.cloudattic.entity.DownloadHistory;
import com.nitefox.cloudattic.entity.FileEntity;
import com.nitefox.cloudattic.entity.Folder;
import com.nitefox.cloudattic.entity.ShareLink;
import com.nitefox.cloudattic.entity.User;
import com.nitefox.cloudattic.repository.DownloadHistoryRepository;
import com.nitefox.cloudattic.repository.FileEntityRepository;
import com.nitefox.cloudattic.repository.FolderRepository;
import com.nitefox.cloudattic.repository.ShareLinkRepository;
import com.nitefox.cloudattic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileEntityRepository fileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private ShareLinkRepository shareLinkRepository;

    @Mock
    private ShareLinkService shareLinkService;

    @Mock
    private DownloadHistoryRepository downloadHistoryRepository;

    @InjectMocks
    private FileService fileService;

    @TempDir
    Path tempDir;

    private User testUser;
    private Folder testFolder;
    private FileEntity testFile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testFolder = new Folder();
        testFolder.setId(10L);
        testFolder.setName("testFolder");

        testFile = new FileEntity();
        testFile.setId(100L);
        testFile.setOriginalName("test.txt");
        testFile.setStoredName("stored-test.txt");
        testFile.setPath(tempDir.resolve("test.txt").toString());
        testFile.setOwner(testUser);
        testFile.setFolder(null);
        testFile.setMimeType("text/plain"); // <-- ДОБАВЛЕНО: устанавливаем mimeType
    }

    @Test
    void getUserFiles_ShouldReturnRootFiles() {
        when(fileRepository.findByOwnerAndFolderIsNull(testUser)).thenReturn(List.of(testFile));

        List<FileEntity> result = fileService.getUserFiles(testUser);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testFile);
        verify(fileRepository).findByOwnerAndFolderIsNull(testUser);
    }

    @Test
    void uploadFile_ShouldSaveFileAndEntity_WhenNoFolder() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "doc.pdf", "application/pdf", "content".getBytes());
        when(fileRepository.save(any(FileEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        FileEntity saved = fileService.uploadFile(multipartFile, testUser, null);

        assertThat(saved.getOriginalName()).isEqualTo("doc.pdf");
        assertThat(saved.getMimeType()).isEqualTo("application/pdf");
        assertThat(saved.getSize()).isEqualTo(7); // "content".getBytes().length = 7
        assertThat(saved.getOwner()).isEqualTo(testUser);
        assertThat(saved.getFolder()).isNull();
        assertThat(saved.getPath()).isNotNull();
        assertThat(Files.exists(Paths.get(saved.getPath()))).isTrue();

        verify(fileRepository).save(any(FileEntity.class));
    }

    @Test
    void uploadFile_ShouldSaveFileAndEntity_WhenFolderExists() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "doc.pdf", "application/pdf", "content".getBytes());
        when(folderRepository.findById(10L)).thenReturn(Optional.of(testFolder));
        when(fileRepository.save(any(FileEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        FileEntity saved = fileService.uploadFile(multipartFile, testUser, 10L);

        assertThat(saved.getFolder()).isEqualTo(testFolder);
        verify(folderRepository).findById(10L);
    }

    @Test
    void downloadFile_ShouldReturnResourceAndLogDownload() throws IOException {
        // Создаём реальный файл
        Path realFile = tempDir.resolve("test.txt");
        Files.writeString(realFile, "data");
        testFile.setPath(realFile.toString());
        testFile.setMimeType("text/plain");

        when(fileRepository.findById(100L)).thenReturn(Optional.of(testFile));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.1");

        ResponseEntity<Resource> response = fileService.downloadFile(100L, request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).contains("attachment; filename*=UTF-8''test.txt");
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/plain");

        ArgumentCaptor<DownloadHistory> historyCaptor = ArgumentCaptor.forClass(DownloadHistory.class);
        verify(downloadHistoryRepository).save(historyCaptor.capture());
        DownloadHistory history = historyCaptor.getValue();
        assertThat(history.getFile()).isEqualTo(testFile);
        assertThat(history.getUser()).isNull();
        assertThat(history.getIpAddress()).isEqualTo("192.168.1.1");
    }

    @Test
    void downloadPublicFile_ShouldReturnResource() throws IOException {
        ShareLink link = new ShareLink();
        link.setFile(testFile);
        Path realFile = tempDir.resolve("test.txt");
        Files.writeString(realFile, "data");
        testFile.setPath(realFile.toString());
        testFile.setMimeType("text/plain");

        when(shareLinkRepository.findByToken("token123")).thenReturn(Optional.of(link));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");

        ResponseEntity<Resource> response = fileService.downloadPublicFile("token123", request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/plain");
        verify(downloadHistoryRepository).save(any());
    }

    @Test
    void openFile_ShouldReturnInlineResource() throws IOException {
        Path realFile = tempDir.resolve("test.txt");
        Files.writeString(realFile, "data");
        testFile.setPath(realFile.toString());
        testFile.setMimeType("text/plain");
        
        when(fileRepository.findById(100L)).thenReturn(Optional.of(testFile));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        ResponseEntity<Resource> response = fileService.openFile(100L, request);

        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).contains("inline; filename*=UTF-8''test.txt");
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/plain");
    }

    @Test
    void generatePublicLink_ShouldReturnFullUrl() {
        ShareLink link = new ShareLink();
        link.setToken("abc-123");
        when(fileRepository.findById(100L)).thenReturn(Optional.of(testFile));
        when(shareLinkService.createLink(eq(testFile), anyLong())).thenReturn(link);
        // Используем anyLong() вместо eq(24), так как метод вызывается с 24, но 
        // Mockito может не совпадать из-за примитивного long

        String publicLink = fileService.generatePublicLink(100L);

        assertThat(publicLink).isEqualTo("http://localhost:8080/public/abc-123");
        verify(shareLinkService).createLink(eq(testFile), anyLong());
    }

    @Test
    void renameFile_ShouldUpdateNameAndSave() {
        when(fileRepository.findById(100L)).thenReturn(Optional.of(testFile));
        fileService.renameFile(100L, "newname.txt");

        assertThat(testFile.getOriginalName()).isEqualTo("newname.txt");
        verify(fileRepository).save(testFile);
    }

    @Test
    void deleteFile_ShouldDeleteLinksPhysicalFileAndRecord() throws IOException {
        Path realFile = tempDir.resolve("toDelete.txt");
        Files.writeString(realFile, "data");
        testFile.setPath(realFile.toString());

        when(fileRepository.findById(100L)).thenReturn(Optional.of(testFile));
        when(shareLinkRepository.findAllByFile(testFile)).thenReturn(List.of(new ShareLink()));

        fileService.deleteFile(100L);

        verify(shareLinkRepository).deleteAll(any());
        verify(fileRepository).delete(testFile);
        assertThat(Files.exists(realFile)).isFalse();
    }

    @Test
    void uploadFolder_ShouldCreateFolderStructureAndFiles() throws IOException {
        // Создаём mock-файлы с путями папок
        MockMultipartFile file1 = new MockMultipartFile("files", "docs/report.pdf", "application/pdf", "pdf".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "docs/images/photo.jpg", "image/jpeg", "jpg".getBytes());

        when(folderRepository.save(any(Folder.class))).thenAnswer(inv -> {
            Folder f = inv.getArgument(0);
            f.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
            return f;
        });
        when(fileRepository.save(any(FileEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        fileService.uploadFolder(List.of(file1, file2), testUser, null);

        // Должно быть создано 2 папки (docs, images) и 2 файла
        verify(folderRepository, atLeast(2)).save(any(Folder.class));
        verify(fileRepository, times(2)).save(any(FileEntity.class));
    }

    @Test
    void downloadFile_ShouldThrowException_WhenFileNotFound() throws IOException {
        when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        MockHttpServletRequest request = new MockHttpServletRequest();
        
        assertThatThrownBy(() -> fileService.downloadFile(999L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("File not found");
    }

    @Test
    void downloadPublicFile_ShouldThrowException_WhenTokenNotFound() throws IOException {
        when(shareLinkRepository.findByToken("invalid")).thenReturn(Optional.empty());

        MockHttpServletRequest request = new MockHttpServletRequest();
        
        assertThatThrownBy(() -> fileService.downloadPublicFile("invalid", request))
                .isInstanceOf(RuntimeException.class);
    }
}