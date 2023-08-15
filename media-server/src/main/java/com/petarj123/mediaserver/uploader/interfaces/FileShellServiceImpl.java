package com.petarj123.mediaserver.uploader.interfaces;

import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import com.petarj123.mediaserver.uploader.file.model.FileType;

import java.util.List;

public interface FileShellServiceImpl {
    void setMaxFileSize(long size);
    void setMaxRequestSize(long size);

    void setAllowedExtensions(FileType type, List<String> extensions) throws InvalidFileExtensionException;
    void removeExtension(FileType type, String extension) throws InvalidFileExtensionException;
}
