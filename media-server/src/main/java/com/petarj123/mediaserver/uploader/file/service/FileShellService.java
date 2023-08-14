package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.interfaces.FileShellServiceImpl;
import com.petarj123.mediaserver.uploader.service.DatabaseShellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

@Service
public class FileShellService implements FileShellServiceImpl {
    @Value("${properties.path}")
    private String PROP_FILE_PATH;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseShellService.class);

    @Override
    public void setMaxFileSize(long size) {
        try {
            Properties properties = new Properties();
            FileInputStream in = new FileInputStream(PROP_FILE_PATH);
            properties.load(in);
            in.close();

            properties.setProperty("spring.servlet.multipart.max-file-size", Long.toString(size));

            FileOutputStream out = new FileOutputStream(PROP_FILE_PATH);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void setMaxRequestSize(long size) {
        try {
            Properties properties = new Properties();
            FileInputStream in = new FileInputStream(PROP_FILE_PATH);
            properties.load(in);
            in.close();

            properties.setProperty("spring.servlet.multipart.max-request-size", Long.toString(size));

            FileOutputStream out = new FileOutputStream(PROP_FILE_PATH);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void setAllowedExtensions(String... extensions) {
        // TODO
    }
}
