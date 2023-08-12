package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.exceptions.InfectedFileException;
import com.petarj123.mediaserver.uploader.interfaces.FileServiceImpl;
import com.petarj123.mediaserver.uploader.service.ClamAVService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService implements FileServiceImpl {
    private static final String serverFolderPath = "/home/petarjankovic/Documents/Server/";
    private final ClamAVService clamAVService;

    @Override
    public ScanResult saveFile(MultipartFile file, String folderName) throws FileException {
        if (file.isEmpty()) {
            throw new FileException("File is empty");
        }

        try {
            Path tempFolderPath = Paths.get(serverFolderPath, "temp");
            if (!Files.exists(tempFolderPath)) {
                Files.createDirectories(tempFolderPath);
            }

            Path tempTargetPath = tempFolderPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));

            // Save the file to the temp directory first
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

            Path finalTargetPath = finalFolderPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
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



}
