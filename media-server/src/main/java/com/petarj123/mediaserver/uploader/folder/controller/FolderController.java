package com.petarj123.mediaserver.uploader.folder.controller;

import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.folder.dto.FolderDTO;
import com.petarj123.mediaserver.uploader.folder.model.Folder;
import com.petarj123.mediaserver.uploader.folder.service.FolderService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @RateLimiter(name = "fileAndFolder")
    public Folder createFolder(@RequestBody FolderDTO request) throws FolderException {
        return folderService.createFolder(request.name(), request.folderId()); // request.folderId() In this case this gets parent folder id
    }
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "fileAndFolder")
    public List<Folder> getAllFolders(@RequestHeader("Authorization") String header) throws FolderException {
        String token = header.substring(7);
        return folderService.getAllFolders(token);
    }
    @GetMapping("/files")
    @RateLimiter(name = "fileAndFolder")
    public List<String> getFilesInFolder(@RequestHeader("Authorization") String header, @RequestBody FolderDTO request) throws FolderException {
        String token = header.substring(7);
        return folderService.getFolderFiles(token, request.folderId());
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RateLimiter(name = "fileAndFolder")
    public void deleteFolder(@RequestBody FolderDTO request) throws FolderException {
        folderService.deleteFolder(request.folderId());
    }
}
