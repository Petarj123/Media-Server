package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.interfaces.FileServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileService implements FileServiceImpl {
    private static final String serverFolderPath = "/home/petarjankovic/Documents/Server/";

    @Override
    public String saveFile(MultipartFile file, String folderName) {

        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file");
        }

        try {
            // Create directory if it doesn't exist
            Path folderPath = Paths.get(serverFolderPath, folderName);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // Resolve ensures the path is combined correctly
            Path targetPath = folderPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));

            // Save the file
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public boolean deleteFile(String filename, String folderName) {
        try {
            Path fileToDelete = Paths.get(serverFolderPath, folderName, filename);
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file " + filename, e);
        }
    }
}
