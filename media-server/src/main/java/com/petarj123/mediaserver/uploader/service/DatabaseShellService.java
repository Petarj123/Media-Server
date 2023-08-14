package com.petarj123.mediaserver.uploader.service;

import com.petarj123.mediaserver.uploader.interfaces.DatabaseCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class DatabaseShellService implements DatabaseCommands {
    @Value("${properties.path}")
    private String PROP_FILE_PATH;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseShellService.class);
    @Value("${spring.data.mongodb.uri}")
    private String mongodbUri;
    @Value("${spring.data.mongodb.database}")
    private String database;
    @Value("${db.backup}")
    private String backupFolder;

    @Override
    public void setDBConnection(String mongodbUri, String database) {
        try {
            Properties properties = new Properties();
            FileInputStream in = new FileInputStream(PROP_FILE_PATH);
            properties.load(in);
            in.close();

            properties.setProperty("spring.data.mongodb.uri", mongodbUri);
            properties.setProperty("spring.data.mongodb.database", database);

            FileOutputStream out = new FileOutputStream(PROP_FILE_PATH);
            properties.store(out, null);
            out.close();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }


    @Override
    public void backupDatabase() {
        try {
            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String backupPath = backupFolder + "/" + currentDateTime;
            String[] command = new String[]{
                    "mongodump",
                    "--uri=" + mongodbUri,
                    "--db=" + database,
                    "--out=" + backupPath
            };
            Process process = Runtime.getRuntime().exec(command);

            process.waitFor();

            if (process.exitValue() == 0) {
                System.out.println("Backup successful!");
            } else {
                System.err.println("Backup failed!");
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.toString());
        }
    }

}
