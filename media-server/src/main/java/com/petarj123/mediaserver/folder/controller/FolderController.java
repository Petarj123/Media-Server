package com.petarj123.mediaserver.folder.controller;

import com.petarj123.mediaserver.folder.dto.FolderDTO;
import com.petarj123.mediaserver.folder.model.Folder;
import com.petarj123.mediaserver.folder.service.FolderService;
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
    public Folder createFolder(@RequestBody FolderDTO request){
        return folderService.createFolder(request.name());
    }
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Folder> getAllFolders(){
        return folderService.getAllFolders();
    }
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteFolder(@RequestBody FolderDTO request){
        return folderService.deleteFolder(request.name());
    }
}
