package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.interfaces.FileServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FileService implements FileServiceImpl {
    private static final String serverFolderPath = "/home/petarjankovic/Documents/Server/";

    @Override
    public String saveFile(MultipartFile file, String folderName) throws FileException {
        if (file.isEmpty()) {
            throw new FileException("File is empty");
        }

        try {
            Path folderPath = Paths.get(serverFolderPath, folderName);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // Resolve ensures the path is combined correctly
            Path targetPath = folderPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));

            if (Files.exists(targetPath)) {
                throw new FileException("File " + file.getOriginalFilename() + " already exists.");
            }

            // Save the file
            Files.copy(file.getInputStream(), targetPath);

            return targetPath.toString();

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

}
