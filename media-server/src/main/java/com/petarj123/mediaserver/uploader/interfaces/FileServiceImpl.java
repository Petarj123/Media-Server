package com.petarj123.mediaserver.uploader.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface FileServiceImpl {
    String saveFile(MultipartFile file, String folderName);
    boolean deleteFile(String filename, String folderName);

}
