package com.petarj123.mediaserver.uploader.folder.controller;

import com.petarj123.mediaserver.auth.exceptions.InvalidEmailException;
import com.petarj123.mediaserver.uploader.DTO.Item;
import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.folder.dto.FolderDTO;
import com.petarj123.mediaserver.uploader.folder.model.Folder;
import com.petarj123.mediaserver.uploader.folder.service.FolderService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/files/{folderId}")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "fileAndFolder")
    public Map<String, List<Item>> getFilesInFolder(@PathVariable String folderId) throws FolderException {
        return folderService.getFolderFiles(folderId);
    }
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "fileAndFolder")
    public Map<String, List<Item>> fetchMainFolder(@RequestHeader("Authorization") String header) throws FolderException, InvalidEmailException {
        String token = header.substring(7);
        return folderService.fetchMainFolder(token);
    }
    @DeleteMapping("/delete/{folderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RateLimiter(name = "fileAndFolder")
    public void deleteFolder(@PathVariable String folderId) throws FolderException {
        folderService.deleteFolder(folderId);
    }
}
