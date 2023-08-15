package com.petarj123.mediaserver.uploader.file.controller;

import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import com.petarj123.mediaserver.uploader.file.model.FileType;
import com.petarj123.mediaserver.uploader.file.service.FileShellService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class FileCommands {

    private final FileShellService fileShellService;

    @ShellMethod(value = "Set maximum file size", key = "set-file-size")
    public void setMaxFileSize(@ShellOption(help = "Size in megabytes") long size) {
        fileShellService.setMaxFileSize(size);
    }

    @ShellMethod(value = "Set maximum request size", key = "set-request-size")
    public void setMaxRequestSize(@ShellOption(help = "Size in megabytes") long size) {
        fileShellService.setMaxRequestSize(size);
    }
    @ShellMethod(value = "Set allowed file extensions for a specific file type", key = {"setExtensions", "se"})
    public String setAllowedExtensions(
            @ShellOption(help = "File type. Supported types: PHOTO, VIDEO, TEXT, PDF") FileType type,
            @ShellOption(help = "File extensions, e.g. .jpg .png") List<String> extensions) {
        try {
            fileShellService.setAllowedExtensions(type, extensions);
            return "Allowed extensions for " + type + " have been updated.";
        } catch (InvalidFileExtensionException e) {
            return "Error: " + e.getMessage();
        }
    }
    @ShellMethod(value = "Remove a specific extension for a file type", key = {"removeExtension", "rmExt"})
    public String removeExtension(
            @ShellOption(help = "File type. Supported types: PHOTO, VIDEO, TEXT, PDF") FileType fileType,
            @ShellOption(help = "File extension to be removed, e.g. .jpg") String extension) {
        try {
            fileShellService.removeExtension(fileType, extension);
            return "Extension " + extension + " has been removed from " + fileType + ".";
        } catch (InvalidFileExtensionException e) {
            return "Error: " + e.getMessage();
        }
    }
}
