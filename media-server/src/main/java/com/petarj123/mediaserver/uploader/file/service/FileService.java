package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import com.petarj123.mediaserver.uploader.file.model.File;
import com.petarj123.mediaserver.uploader.file.model.SanitizedFile;
import com.petarj123.mediaserver.uploader.file.repository.FileRepository;
import com.petarj123.mediaserver.uploader.interfaces.FileServiceImpl;
import com.petarj123.mediaserver.uploader.service.ClamAVService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService implements FileServiceImpl {
    @Value("${fileStorage.path}")
    private String serverFolderPath;

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final FileRepository fileRepository;
    private final FileSanitizer fileSanitizer;
    private final FileSystemService fileSystemService;
    @Override
    public ScanResult saveFile(MultipartFile multipartFile, String folderName) throws FileException, InvalidFileExtensionException, EncoderException, IOException {
        // 1. Ensure that the provided file isn't empty
        fileSystemService.checkFileIsEmpty(multipartFile);

        // 2. Sanitize the file's name to prevent possible security risks
        SanitizedFile sanitizedFileName = fileSanitizer.sanitizeFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        // 3. Prepare the path in the temporary directory where the file will be initially stored
        Path tempTargetPath = fileSystemService.prepareTemporaryPath(sanitizedFileName);

        try {
            // 4. Copy the file from the client's request to the temporary location on the server
            Files.copy(multipartFile.getInputStream(), tempTargetPath);

            // 5. Scan the temporarily stored file for viruses using ClamAV
            fileSystemService.scanWithClamAV(tempTargetPath, multipartFile);

            // 6. Prepare the final storage path for the file after validation
            Path finalTargetPath = fileSystemService.prepareFinalPath(sanitizedFileName, folderName);

            // 7. If the file is a video or photo, extract its metadata from the temporary location
            Map<String, Object> metadata = fileSystemService.extractFileMetadata(tempTargetPath, sanitizedFileName.fileType);

            // 8. Build the file's metadata and save it to the database
            fileSystemService.buildAndSaveFile(sanitizedFileName, finalTargetPath, sanitizedFileName.fileType, metadata);

            // 9. Move the validated file from the temporary directory to its final location
            Files.move(tempTargetPath, finalTargetPath);

            // 10. Return the result of the operation, indicating the file's final path and confirming its safety
            return new ScanResult(finalTargetPath.toString(), true);
        } finally {
            // Delete the file from the temporary directory, regardless of whether an exception was thrown or not
            Files.deleteIfExists(tempTargetPath);
        }
    }




    @Override
    public boolean deleteFile(String filename, String folderName) throws FileException {
        File file = fileRepository.findByFileName(filename)
                .orElseThrow(() -> new FileException("File " + filename + " not found in the database."));

        Path fileToDelete = Paths.get(serverFolderPath, folderName, filename);
        if (!Files.exists(fileToDelete)) {
            throw new FileException("File " + filename + " does not exist on the file system.");
        }

        try {
            Files.delete(fileToDelete);
            fileRepository.delete(file);
            return true;
        } catch (IOException e) {
            throw new FileException("Failed to delete file " + filename + ". Error: " + e.getMessage());
        }
    }

    @Override
    public void moveFiles(List<String> files, String currentFolder, String newFolder) throws FileException {
        Path currentFolderPath = Paths.get(serverFolderPath, currentFolder);
        if (!Files.exists(currentFolderPath) || !Files.isDirectory(currentFolderPath)) {
            throw new FileException("Source folder " + currentFolder + " does not exist or is not a directory.");
        }

        Path newFolderPath = Paths.get(serverFolderPath, newFolder);
        if (!Files.exists(newFolderPath) || !Files.isDirectory(newFolderPath)) {
            throw new FileException("Target folder " + newFolder + " does not exist or is not a directory.");
        }

        for (String file : files) {
            File storedFile = fileRepository.findByFileName(file).orElseThrow(() -> new FileException("File " + file + " does not exist on the file system."));
            Path storedFileLocation = Paths.get(storedFile.getFilePath());

            Path targetPath = newFolderPath.resolve(storedFile.getFileName());

            if (Files.exists(targetPath)) {
                throw new FileException("File " + file + " already exists in " + newFolder + ".");
            }

            try {
                Files.move(storedFileLocation, targetPath);
                storedFile.setFilePath(targetPath.toString());
                fileRepository.save(storedFile);
            } catch (IOException e) {
                logger.error(String.valueOf(e));
                throw new FileException("Error moving file " + file + " to " + newFolder);
            }
        }
    }


}
