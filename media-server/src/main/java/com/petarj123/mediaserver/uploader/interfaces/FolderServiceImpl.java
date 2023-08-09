package com.petarj123.mediaserver.uploader.interfaces;

import com.petarj123.mediaserver.uploader.folder.model.Folder;

import java.util.List;

public interface FolderServiceImpl {
    Folder createFolder(String name);
    List<Folder> getAllFolders();
    boolean deleteFolder(String folderName);
}
