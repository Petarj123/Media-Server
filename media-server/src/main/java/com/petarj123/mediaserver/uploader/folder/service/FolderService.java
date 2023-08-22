package com.petarj123.mediaserver.uploader.folder.service;

import com.petarj123.mediaserver.auth.exceptions.InvalidEmailException;
import com.petarj123.mediaserver.auth.jwt.service.JwtService;
import com.petarj123.mediaserver.auth.user.model.User;
import com.petarj123.mediaserver.auth.user.repository.UserRepository;
import com.petarj123.mediaserver.uploader.DTO.Item;
import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.file.model.FileType;
import com.petarj123.mediaserver.uploader.file.repository.FileRepository;
import com.petarj123.mediaserver.uploader.folder.model.Folder;
import com.petarj123.mediaserver.uploader.folder.repository.FolderRepository;
import com.petarj123.mediaserver.uploader.interfaces.FolderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FolderService implements FolderServiceImpl {

    private final FolderRepository folderRepository;
    private final JwtService jwtService;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

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
                    .fileType(FileType.FOLDER)
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
    public Map<String, List<Item>> fetchMainFolder(String token) throws FolderException, InvalidEmailException {
        String email = jwtService.getEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidEmailException("Invalid email."));
        Folder folder = folderRepository.findByPath(user.getUserFolderPath()).orElseThrow(() -> new FolderException("Unable to find folder."));

        List<Item> list = new ArrayList<>();

        // Fetch all child folders in a single call
        List<Folder> childFolders = folderRepository.findAllByIdIn(folder.getChildFolderIds());
        for (Folder childFolder : childFolders) {
            list.add(convertToItem(childFolder));
        }

        // Fetch all child files in a single call
        List<com.petarj123.mediaserver.uploader.file.model.File> childFiles = fileRepository.findAllByIdIn(folder.getChildFileIds());
        for (com.petarj123.mediaserver.uploader.file.model.File childFile : childFiles) {
            list.add(convertToItem(childFile));
        }

        Map<String, List<Item>> result = new HashMap<>();
        result.put(folder.getId(), list);

        return result;
    }

    @Override
    public Map<String, List<Item>> getFolderFiles(String folderId) throws FolderException {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderException("Folder not found."));

        List<Item> list = new ArrayList<>();

        // Fetch all child folders in a single call
        List<Folder> childFolders = folderRepository.findAllByIdIn(folder.getChildFolderIds());
        for (Folder childFolder : childFolders) {
            list.add(convertToItem(childFolder));
        }

        // Fetch all child files in a single call
        List<com.petarj123.mediaserver.uploader.file.model.File> childFiles = fileRepository.findAllByIdIn(folder.getChildFileIds());
        for (com.petarj123.mediaserver.uploader.file.model.File childFile : childFiles) {
            list.add(convertToItem(childFile));
        }

        Map<String, List<Item>> result = new HashMap<>();
        result.put(folder.getId(), list);
        return result;
    }

    @Override
    public void deleteFolder(String folderId) throws FolderException {
        if (folderId == null || folderId.trim().isEmpty()) {
            throw new FolderException("Invalid folder ID.");
        }

        Folder folderToDelete = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderException("Folder not found."));

        File folderFile = new File(folderToDelete.getPath());
        if (folderFile.exists() && folderFile.isDirectory()) {
            try {
                FileUtils.deleteDirectory(folderFile);

                if (folderToDelete.getParentFolderId() != null) {
                    Folder parentFolder = folderRepository.findById(folderToDelete.getParentFolderId())
                            .orElseThrow(() -> new FolderException("Parent folder not found."));
                    if (parentFolder.getChildFolderIds() != null) {
                        parentFolder.getChildFolderIds().remove(folderId);
                        folderRepository.save(parentFolder);
                    }
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
    private Item convertToItem(Folder folder) {
        return new Item(folder.getId(), folder.getName(), folder.getPath(), folder.getFileType());
    }
    private Item convertToItem(com.petarj123.mediaserver.uploader.file.model.File file){
        return new Item(file.getId(), file.getFileName(), file.getFilePath(), file.getFileType());

    }
}
