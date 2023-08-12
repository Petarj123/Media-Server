package com.petarj123.mediaserver.uploader.interfaces;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import org.springframework.web.multipart.MultipartFile;

public interface FileServiceImpl {
    ScanResult saveFile(MultipartFile file, String folderName) throws FileException;
    boolean deleteFile(String filename, String folderName) throws FileException;

}
