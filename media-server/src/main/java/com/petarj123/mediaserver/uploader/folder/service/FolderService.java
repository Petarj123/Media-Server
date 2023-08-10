package com.petarj123.mediaserver.uploader.folder.service;

import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.folder.model.Folder;
import com.petarj123.mediaserver.uploader.interfaces.FolderServiceImpl;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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
public class FolderService implements FolderServiceImpl {

    private static final String serverFolderPath = "/home/petarjankovic/Documents/Server/";

    @Override
    public Folder createFolder(String name) throws FolderException {
        try {
            Path rootPath = Paths.get(serverFolderPath);
            if (!Files.exists(rootPath)) {
                Files.createDirectories(rootPath);
            }

            Path folderPath = rootPath.resolve(name);
            if (!Files.exists(folderPath)) {
                Files.createDirectory(folderPath);
                return Folder.builder()
                        .name(name)
                        .createdAt(new Date())
                        .build();
            } else {
                throw new FolderException("Folder " + name + " already exists.");
            }
        } catch (IOException e) {
            throw new FolderException("Failed to create folder. Error: " + e.getMessage());
        }
    }

    @Override
    public List<Folder> getAllFolders() throws FolderException {
        File baseDirectory = new File(serverFolderPath);
        if (!baseDirectory.exists()) {
            throw new FolderException("Server folder path does not exist.");
        }

        File[] folders = baseDirectory.listFiles(File::isDirectory);
        List<Folder> folderList = new ArrayList<>();
        if (folders != null) {
            for (File folder : folders) {
                folderList.add(new Folder(folder.getName(), new Date(folder.lastModified())));
            }
        }
        return folderList;
    }

    @Override
    public List<String> getFolderFiles(String folderName) throws FolderException {
        File folder = new File(serverFolderPath + File.separator + folderName);

        if (!folder.isDirectory()) {
            throw new FolderException("Given path is not a directory.");
        }

        File[] files = folder.listFiles(File::isFile);
        List<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }

    @Override
    public boolean deleteFolder(String folderName) throws FolderException {
        File folder = new File(serverFolderPath + folderName);
        if (folder.exists() && folder.isDirectory()) {
            try {
                FileUtils.deleteDirectory(folder); // Apache Commons IO utility
                return true;
            } catch (IOException e) {
                throw new FolderException("Failed to delete folder. Error: " + e.getMessage());
            }
        } else {
            throw new FolderException("Folder does not exist or is not a directory.");
        }
    }

}
