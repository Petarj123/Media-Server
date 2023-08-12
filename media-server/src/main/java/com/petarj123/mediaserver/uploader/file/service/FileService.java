package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.exceptions.InfectedFileException;
import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import com.petarj123.mediaserver.uploader.interfaces.FileServiceImpl;
import com.petarj123.mediaserver.uploader.service.ClamAVService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService implements FileServiceImpl {
    private static final String serverFolderPath = "/home/petarjankovic/Documents/Server/";
    private final ClamAVService clamAVService;

    @Override
    public ScanResult saveFile(MultipartFile file, String folderName) throws FileException, InvalidFileExtensionException {
        if (file.isEmpty()) {
            throw new FileException("File is empty");
        }

        // Sanitize the filename before any file operations
        String sanitizedFileName = sanitizeFileName(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            Path tempFolderPath = Paths.get(serverFolderPath, "temp");
            if (!Files.exists(tempFolderPath)) {
                Files.createDirectories(tempFolderPath);
            }

            Path tempTargetPath = tempFolderPath.resolve(Objects.requireNonNull(sanitizedFileName));

            // Save the file to the temp directory
            Files.copy(file.getInputStream(), tempTargetPath);

            // Scan the file with ClamAV
            ScanResult scanResult = clamAVService.scanFile(tempTargetPath);
            if (!scanResult.isClean()) {
                Files.delete(tempTargetPath);
                throw new InfectedFileException("File " + file.getOriginalFilename() + " is infected and has been deleted.");
            }

            Path finalFolderPath = Paths.get(serverFolderPath, folderName);
            if (!Files.exists(finalFolderPath)) {
                Files.createDirectories(finalFolderPath);
            }

            Path finalTargetPath = finalFolderPath.resolve(Objects.requireNonNull(sanitizedFileName));
            if (Files.exists(finalTargetPath)) {
                throw new FileException("File " + file.getOriginalFilename() + " already exists.");
            }

            // Move the scanned and safe file to the final directory
            Files.move(tempTargetPath, finalTargetPath);

            return new ScanResult(finalTargetPath.toString(), true);

        } catch (IOException e) {
            throw new FileException("Failed to store file " + file.getOriginalFilename() + ". Error: " + e.getMessage());
        }
    }



    @Override
    public boolean deleteFile(String filename, String folderName) throws FileException {
        Path fileToDelete = Paths.get(serverFolderPath, folderName, filename);

        if (!Files.exists(fileToDelete)) {
            throw new FileException("File " + filename + " does not exist.");
        }

        try {
            Files.delete(fileToDelete);
            return true;
        } catch (IOException e) {
            throw new FileException("Failed to delete file " + filename + ". Error: " + e.getMessage());
        }
    }
    // TODO Vidi sta treba da se desi ako fajlovi imaju isto ime, ali drugaciji sadrzaj.
    private String sanitizeFileName(String originalFilename) throws InvalidFileExtensionException {
        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Collapse multiple underscores into one
        sanitized = sanitized.replaceAll("_{2,}", "_");

        // Ensure file names don't start or end with a dot or underscore
        sanitized = sanitized.replaceAll("^[._]", "");
        sanitized = sanitized.replaceAll("[._]$", "");

        // Ensure file names don't contain sequences like ".."
        sanitized = sanitized.replace("..", ".");

        // Check extensions
        String[] allowedExtensions = {".txt", ".png", ".jpg", ".pdf"};
        String finalSanitized = sanitized;
        boolean isValidExtension = Arrays.stream(allowedExtensions)
                .anyMatch(finalSanitized::endsWith);
        if (!isValidExtension) {
            throw new InvalidFileExtensionException("Unsupported file extension");
        }

        // Length limitation
        int MAX_FILENAME_LENGTH = 255;
        if (sanitized.length() > MAX_FILENAME_LENGTH) {
            throw new IllegalArgumentException("File name is too long");
        }

        return sanitized;
    }


}
