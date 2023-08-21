package com.petarj123.mediaserver.uploader.controller;

import com.petarj123.mediaserver.uploader.service.FileUploaderShellService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class FileUploaderCommands {

    private final FileUploaderShellService fileUploaderShellService;

    @ShellMethod(value = "Change server address", key = "sa")
    public void changeServerAddress(String address){
        fileUploaderShellService.changeServerAddress(address);
    }
    @ShellMethod(value = "Change server port", key = "port")
    public void changeServerPort(int port){
        fileUploaderShellService.changeServerPort(port);
    }
}
