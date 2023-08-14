package com.petarj123.mediaserver.uploader.interfaces;

public interface DatabaseCommands {
    void setDBConnection(String mongodbUri, String database);
    void backupDatabase();
}
