package com.petarj123.mediaserver.uploader.folder.service;

import com.petarj123.mediaserver.auth.jwt.service.JwtService;
import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.folder.model.Folder;
import com.petarj123.mediaserver.uploader.folder.repository.FolderRepository;
import com.petarj123.mediaserver.uploader.interfaces.FolderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
@RequiredArgsConstructor
public class FolderService implements FolderServiceImpl {

    private final FolderRepository folderRepository;
    private final JwtService jwtService;

    @Override
    public Folder createFolder(String name, String parentFolderId) throws FolderException {
        Folder parentFolder = folderRepository.findById(parentFolderId)
                .orElseThrow(() -> new FolderException("Parent folder not found."));
        String sanitizedName = sanitizeFolderName(name);
        String folderPath = parentFolder.getPath() + File.separator + sanitizedName;
        File newFolder = new File(folderPath);

        if (newFolder.exists()) {
            throw new FolderException("Folder already exists.");
        }

        try {
            Folder folder = Folder.builder()
                    .name(sanitizedName)
                    .path(folderPath)
                    .parentFolderId(parentFolderId)
                    .createdAt(new Date())
                    .build();

            Folder savedFolder = folderRepository.save(folder);

            if (parentFolder.getChildFolderIds() == null) {
                parentFolder.setChildFolderIds(new ArrayList<>());
            }
            parentFolder.getChildFolderIds().add(savedFolder.getId());

            folderRepository.save(parentFolder);
            Files.createDirectory(newFolder.toPath());
            return savedFolder;
        } catch (IOException e) {
            throw new FolderException("Failed to create folder. Error: " + e.getMessage());
        }
    }

    @Override
    public List<Folder> getAllFolders(String token) throws FolderException {
        Folder userMainFolder = getUserMainFolder(token);
        return folderRepository.findAllByPathStartingWith(userMainFolder.getPath());
    }

    @Override
    public List<String> getFolderFiles(String token, String folderId) throws FolderException {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderException("Folder not found."));

        // TODO Must return all folders and files from this folder

        return null;
    }

    @Override
    public void deleteFolder(String folderId) throws FolderException {
        Folder folderToDelete = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderException("Folder not found."));

        File folderFile = new File(folderToDelete.getPath());
        if (folderFile.exists() && folderFile.isDirectory()) {
            try {
                FileUtils.deleteDirectory(folderFile);

                if (folderToDelete.getParentFolderId() != null) {
                    Folder parentFolder = folderRepository.findById(folderToDelete.getParentFolderId())
                            .orElseThrow(() -> new FolderException("Parent folder not found."));
                    parentFolder.getChildFolderIds().remove(folderId);
                    folderRepository.save(parentFolder);
                }

                folderRepository.delete(folderToDelete);
            } catch (IOException e) {
                throw new FolderException("Failed to delete folder. Error: " + e.getMessage());
            }
        } else {
            throw new FolderException("Folder does not exist or is not a directory.");
        }
    }

    private String sanitizeFolderName(String originalName) {
        // Replace spaces with underscores
        String sanitized = originalName.replaceAll(" ", "_");

        // Remove non-alphanumeric, non-underscore, and non-hyphen characters
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9_-]", "");

        // Ensure name doesn't start with a dot
        if (sanitized.startsWith(".")) {
            sanitized = sanitized.substring(1);
        }

        // Limit the length if needed
        int maxLength = 255; // for example
        if (sanitized.length() > maxLength) {
            sanitized = sanitized.substring(0, maxLength);
        }

        return sanitized;
    }
    private Folder getUserMainFolder(String token) throws FolderException {
        String userEmail = jwtService.getEmail(token);
        return folderRepository.findByName(userEmail)
                .orElseThrow(() -> new FolderException("User's main folder not found."));
    }
}
