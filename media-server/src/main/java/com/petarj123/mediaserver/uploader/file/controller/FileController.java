package com.petarj123.mediaserver.uploader.file.controller;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ScanResult> upload(@RequestParam("files") MultipartFile[] files, @RequestParam("folderName") String folderName) throws FileException {
        List<ScanResult> scanResults = new ArrayList<>();
        for (MultipartFile file : files) {
            scanResults.add(fileService.saveFile(file, folderName));
        }
        return scanResults;
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean delete(@RequestParam("fileName") String fileName, @RequestParam("folderName") String folderName) throws FileException {
        return fileService.deleteFile(fileName, folderName);
    }
}
