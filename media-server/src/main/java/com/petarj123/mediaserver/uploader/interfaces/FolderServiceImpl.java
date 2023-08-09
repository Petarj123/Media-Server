package com.petarj123.mediaserver.uploader.interfaces;

import java.util.List;

public interface FolderServiceImpl {
    com.petarj123.mediaserver.folder.model.Folder createFolder(String name);
    List<com.petarj123.mediaserver.folder.model.Folder> getAllFolders();
    boolean deleteFolder(String folderName);
}
