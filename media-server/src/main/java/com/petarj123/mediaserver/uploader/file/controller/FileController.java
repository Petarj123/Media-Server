package com.petarj123.mediaserver.uploader.file.controller;

import com.petarj123.mediaserver.uploader.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("folderName") String folderName) {
        return fileService.saveFile(file, folderName);
    }
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean delete(@RequestParam("fileName") String fileName, @RequestParam("folderName") String folderName) {
        return fileService.deleteFile(fileName, folderName);
    }
}
