package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.exceptions.InfectedFileException;
import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import com.petarj123.mediaserver.uploader.file.model.File;
import com.petarj123.mediaserver.uploader.file.model.SanitizedFile;
import com.petarj123.mediaserver.uploader.file.repository.FileRepository;
import com.petarj123.mediaserver.uploader.folder.model.Folder;
import com.petarj123.mediaserver.uploader.folder.repository.FolderRepository;
import com.petarj123.mediaserver.uploader.interfaces.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService implements FileServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final FileRepository fileRepository;
    private final FileSanitizer fileSanitizer;
    private final FileSystemService fileSystemService;
    private final FolderRepository folderRepository;
    @Override
    @CacheEvict(value = {"mainFolder", "folder"}, allEntries = true)
    public ScanResult saveFile(MultipartFile multipartFile, String folderId) throws FileException, InvalidFileExtensionException, IOException {
        // 1. Check if the file is empty
        fileSystemService.checkFileIsEmpty(multipartFile);

        // 2. Sanitize the file's name
        SanitizedFile sanitizedFileName = fileSanitizer.sanitizeFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        // 3. Prepare a temporary path
        Path tempTargetPath = fileSystemService.prepareTemporaryPath(sanitizedFileName);

        try {
            // 4. Copy the file
            Files.copy(multipartFile.getInputStream(), tempTargetPath);

            // 5. Scan the file
            fileSystemService.scanWithClamAV(tempTargetPath, multipartFile);

            // 6. Get folder by its ID
            Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new RuntimeException("Folder not found"));

            // 7. Prepare the final storage path using folder's name
            Path finalTargetPath = fileSystemService.prepareFinalPath(sanitizedFileName, folder.getPath());

            // 7.1 Check if a file with the same name and path already exists in the database
            long count = fileRepository.countByFileNameAndFilePath(sanitizedFileName.name, finalTargetPath.toString());
            if (count > 0) {
                Files.deleteIfExists(tempTargetPath);
                throw new FileException("File with the same name already exists.");
            }

            // 8. Extract metadata
            Map<String, Object> metadata = fileSystemService.extractFileMetadata(tempTargetPath, sanitizedFileName.fileType);

            // 9. Build and save the file's metadata to the database and link to folder
            fileSystemService.buildAndSaveFile(sanitizedFileName, finalTargetPath, sanitizedFileName.fileType, metadata, folder);

            // 10. Move the validated file
            Files.move(tempTargetPath, finalTargetPath);

            // 11. Return the result
            return new ScanResult(finalTargetPath.toString(), true);

        } catch (InfectedFileException | EncoderException e) {
            // If the file is infected
            Files.deleteIfExists(tempTargetPath);
            throw new RuntimeException("The uploaded file is infected!", e);
        }
        finally {
            Files.deleteIfExists(tempTargetPath);
        }
    }

    @Override
    @CacheEvict(value = {"mainFolder", "folder"}, allEntries = true)
    public boolean deleteFile(String filename, String folderId) throws FileException, FolderException {
        File fileToDelete = fileRepository.findByFileNameAndFolderId(filename, folderId)
                .orElseThrow(() -> new FileException("File " + filename + " not found in the database."));

        // Physically delete the file from the file system
        java.io.File physicalFile = new java.io.File(fileToDelete.getFilePath());
        if (physicalFile.exists()) {
            if (!physicalFile.delete()) {
                throw new FileException("Failed to delete the physical file.");
            }
        } else {
            throw new FileException("File " + filename + " not found in the storage.");
        }

        // Remove the file's reference from the parent folder's childFileIds list
        Folder parentFolder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderException("Parent folder not found in the database."));
        if (parentFolder.getChildFileIds() != null) {
            parentFolder.getChildFileIds().remove(fileToDelete.getId());
            folderRepository.save(parentFolder);
        }

        fileRepository.delete(fileToDelete);

        return true;
    }
    // TODO Test
    @Override
    @CacheEvict(value = {"mainFolder", "folder"}, allEntries = true)
    public void moveFiles(List<String> files, String currentFolderId, String newFolderId) throws FileException {
        // Find source and target folders
        Folder sourceFolder = folderRepository.findById(currentFolderId)
                .orElseThrow(() -> new FileException("Source folder with ID " + currentFolderId + " does not exist."));
        Folder targetFolder = folderRepository.findById(newFolderId)
                .orElseThrow(() -> new FileException("Target folder with ID " + newFolderId + " does not exist."));

        Path sourceFolderPath = Paths.get(sourceFolder.getPath());
        Path targetFolderPath = Paths.get(targetFolder.getPath());

        for (String fileId : files) {
            File storedFile = fileRepository.findById(fileId)
                    .orElseThrow(() -> new FileException("File with ID " + fileId + " does not exist in the database."));

            Path storedFileLocation = Paths.get(storedFile.getFilePath());
            Path targetPath = targetFolderPath.resolve(storedFile.getFileName());

            if (Files.exists(targetPath)) {
                throw new FileException("File " + storedFile.getFileName() + " already exists in " + targetFolder.getName() + ".");
            }

            try {
                Files.move(storedFileLocation, targetPath);
                storedFile.setFilePath(targetPath.toString());
                storedFile.setFolderId(newFolderId);
                fileRepository.save(storedFile);

                // Update childFileIds for source and target folders
                if (sourceFolder.getChildFileIds() != null) {
                    sourceFolder.getChildFileIds().remove(fileId);
                    folderRepository.save(sourceFolder);
                }

                if (targetFolder.getChildFileIds() == null) {
                    targetFolder.setChildFileIds(new ArrayList<>());
                }
                targetFolder.getChildFileIds().add(fileId);
                folderRepository.save(targetFolder);

            } catch (IOException e) {
                logger.error(String.valueOf(e));
                throw new FileException("Error moving file " + storedFile.getFileName() + " to " + targetFolder.getName());
            }
        }
    }
}
