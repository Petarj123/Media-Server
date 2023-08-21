package com.petarj123.mediaserver.uploader.file.controller;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import com.petarj123.mediaserver.uploader.file.service.FileService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @RateLimiter(name = "fileAndFolder")
    public List<ScanResult> upload(@RequestParam("files") MultipartFile[] files, @RequestParam("folderId") String folderId) throws FileException, InvalidFileExtensionException, EncoderException, IOException {
        List<ScanResult> scanResults = new ArrayList<>();
        for (MultipartFile file : files) {
            scanResults.add(fileService.saveFile(file, folderId));
        }
        return scanResults;
    }
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RateLimiter(name = "fileAndFolder")
    public boolean delete(@RequestParam("fileName") String fileName, @RequestParam("folderId") String folderName) throws FileException, FolderException {
        return fileService.deleteFile(fileName, folderName);
    }
    @PutMapping("/move")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "fileAndFolder")
    public void move(@RequestParam("files") List<String> files, @RequestParam("currentFolder") String currentFolder, @RequestParam("newFolder") String newFolder) throws FileException {
        fileService.moveFiles(files, currentFolder, newFolder);
    }
}
