package com.petarj123.mediaserver.uploader.interfaces;

import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.folder.model.Folder;

import java.util.List;

public interface FolderServiceImpl {
    Folder createFolder(String name) throws FolderException;
    List<Folder> getAllFolders() throws FolderException;
    List<String> getFolderFiles(String folderName) throws FolderException;
    boolean deleteFolder(String folderName) throws FolderException;
}
