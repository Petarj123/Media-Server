package com.petarj123.mediaserver.uploader.interfaces;

import com.petarj123.mediaserver.auth.exceptions.InvalidEmailException;
import com.petarj123.mediaserver.uploader.DTO.Item;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.folder.model.Folder;

import java.util.List;
import java.util.Map;

public interface FolderServiceImpl {
    Folder createFolder(String name, String parentFolderId) throws FolderException;
    Map<String, List<Item>> fetchMainFolder(String token) throws FolderException, InvalidEmailException, FileException;
    Map<String, List<Item>> getFolderFiles(String folderName) throws FolderException;
    void deleteFolder(String folderName) throws FolderException;
}
