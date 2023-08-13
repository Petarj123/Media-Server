package com.petarj123.mediaserver.uploader.interfaces;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.util.List;

public interface FileServiceImpl {
    ScanResult saveFile(MultipartFile file, String folderName) throws FileException, InvalidFileExtensionException, EncoderException, IOException;
    boolean deleteFile(String filename, String folderName) throws FileException;
    void moveFiles(List<String> filename, String currentFolder, String newFolder) throws FileException;

}
