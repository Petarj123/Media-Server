package com.petarj123.mediaserver.folder.service;

import com.petarj123.mediaserver.folder.model.Folder;
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
    public Folder createFolder(String name) {
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
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Folder> getAllFolders() {
        File baseDirectory = new File(serverFolderPath);
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
    public boolean deleteFolder(String folderName) {
        File folder = new File(serverFolderPath + folderName);
        if (folder.exists() && folder.isDirectory()) {
            try {
                FileUtils.deleteDirectory(folder); // Apache Commons IO utility
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
