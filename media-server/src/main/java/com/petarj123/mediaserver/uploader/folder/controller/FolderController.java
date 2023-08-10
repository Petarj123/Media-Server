package com.petarj123.mediaserver.uploader.folder.controller;

import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.folder.dto.FolderDTO;
import com.petarj123.mediaserver.uploader.folder.model.Folder;
import com.petarj123.mediaserver.uploader.folder.service.FolderService;
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
    public Folder createFolder(@RequestBody FolderDTO request) throws FolderException {
        return folderService.createFolder(request.name());
    }
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Folder> getAllFolders() throws FolderException {
        return folderService.getAllFolders();
    }
    @GetMapping("/{folderName}/files")
    public List<String> getFilesInFolder(@PathVariable String folderName) throws FolderException {
        return folderService.getFolderFiles(folderName);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean deleteFolder(@RequestBody FolderDTO request) throws FolderException {
        return folderService.deleteFolder(request.name());
    }
}
