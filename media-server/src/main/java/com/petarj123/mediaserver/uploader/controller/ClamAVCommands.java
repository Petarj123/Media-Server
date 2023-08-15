package com.petarj123.mediaserver.uploader.controller;

import com.petarj123.mediaserver.uploader.service.ClamAVShellService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class ClamAVCommands {

    private final ClamAVShellService clamAVShellService;

    @ShellMethod(value = "Enable ClamAV", key = {"enableClamAV", "eav"})
    public void enableClamAVShell() {
        clamAVShellService.enableClamAV();
        System.out.println("ClamAV enabled.");
    }

    @ShellMethod(value = "Disable ClamAV", key = {"disableClamAV", "dav"})
    public void disableClamAVShell() {
        clamAVShellService.disableClamAV();
        System.out.println("ClamAV disabled.");
    }

    @ShellMethod(value = "Enable Scans", key = {"enableScans", "es"})
    public void enableScansShell() {
        clamAVShellService.enableScans();
        System.out.println("Scans enabled.");
    }

    @ShellMethod(value = "Disable Scans", key = {"disableScans", "ds"})
    public void disableScansShell() {
        clamAVShellService.disableScans();
        System.out.println("Scans disabled.");
    }

    @ShellMethod(value = "Schedule Scans", key = {"scheduleScans", "ss"})
    public void scheduleScansShell(long ms) {
        clamAVShellService.scheduleScans(ms);
        System.out.println("Scans scheduled for every " + ms + " milliseconds.");
    }

    @ShellMethod(value = "Set ClamAV port", key = {"setPort", "sp"})
    public void setPortShell(int port) {
        clamAVShellService.setPort(port);
        System.out.println("Port set to: " + port);
    }

    @ShellMethod(value = "Set ClamAV host", key = {"setHost", "sh"})
    public void setHostShell(String host) {
        clamAVShellService.setHost(host);
        System.out.println("Host set to: " + host);
    }

}
