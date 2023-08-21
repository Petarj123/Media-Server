package com.petarj123.mediaserver.uploader.interfaces;

import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.folder.model.Folder;

import java.util.List;

public interface FolderServiceImpl {
    Folder createFolder(String name, String parentFolderId) throws FolderException;
    List<Folder> getAllFolders(String token) throws FolderException;
    List<String> getFolderFiles(String token, String folderName) throws FolderException;
    void deleteFolder(String folderName) throws FolderException;
}
