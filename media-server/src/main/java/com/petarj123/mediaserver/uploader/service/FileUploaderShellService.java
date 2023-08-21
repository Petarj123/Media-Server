package com.petarj123.mediaserver.uploader.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

@Service
public class FileUploaderShellService {

    @Value("${properties.path}")
    private String PROP_FILE_PATH;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseShellService.class);


    public void changeServerAddress(String host) {
        updateProperty("server.address", host);
    }

    public void changeServerPort(int port) {
        updateProperty("server.port", String.valueOf(port));
    }

    private void updateProperty(String key, String value) {
        Properties properties = new Properties();
        String fullPath = PROP_FILE_PATH + "/application.properties";
        try (InputStream input = new FileInputStream(fullPath)) {
            properties.load(input);
            properties.setProperty(key, value);
            try (OutputStream output = new FileOutputStream(fullPath)) {
                properties.store(output, null);
            }
        } catch (IOException ex) {
            logger.error("Error updating property", ex);
        }
    }
}
