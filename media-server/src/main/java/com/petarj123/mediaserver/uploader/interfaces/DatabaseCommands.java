package com.petarj123.mediaserver.uploader.interfaces;

import org.springframework.scheduling.annotation.Scheduled;

public interface DatabaseCommands {
    void connect(String mongodbUri, String database);
    void backupDatabase();
}
