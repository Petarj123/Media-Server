package com.petarj123.mediaserver.uploader.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageImpl {
    String storeFile(MultipartFile file);
    Resource loadFileAsResource(String fileName);
    boolean deleteFile(String fileName);
}
