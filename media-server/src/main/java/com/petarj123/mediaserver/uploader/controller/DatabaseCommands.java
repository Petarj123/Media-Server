package com.petarj123.mediaserver.uploader.controller;

import com.petarj123.mediaserver.uploader.service.DatabaseShellService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class DatabaseCommands {
    private final DatabaseShellService databaseShellService;

    @ShellMethod(value = "Connect", key = {"connect"})
    public void createDatabase(String uri, String database) {
        databaseShellService.connect(uri, database);
    }
    @ShellMethod(value = "Backup", key = {"backup"})
    public void backupDatabase() {
        databaseShellService.backupDatabase();
    }
}
