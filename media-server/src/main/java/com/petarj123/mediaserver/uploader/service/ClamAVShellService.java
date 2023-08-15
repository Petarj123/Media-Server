package com.petarj123.mediaserver.uploader.service;

import com.petarj123.mediaserver.uploader.interfaces.ClamAVShellServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class ClamAVShellService implements ClamAVShellServiceImpl {
    @Value("${properties.path}")
    private String PROP_FILE_PATH;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseShellService.class);
    private final ClamAVService clamAVService;

    @Override
    public void enableClamAV() {
        try{
            Properties properties = new Properties();
            String path = PROP_FILE_PATH + "/application.properties";
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();

            properties.setProperty("clamav.enabled", "true");

            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void disableClamAV() {
        try{
            Properties properties = new Properties();
            String path = PROP_FILE_PATH + "/application.properties";
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();

            properties.setProperty("clamav.enabled", "false");

            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void enableScans() {
        try{
            Properties properties = new Properties();
            String path = PROP_FILE_PATH + "/application.properties";
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();

            properties.setProperty("clamav.scans.enabled", "true");

            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void disableScans() {
        try{
            Properties properties = new Properties();
            String path = PROP_FILE_PATH + "/application.properties";
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();

            properties.setProperty("clamav.scans.enabled", "false");

            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void scheduleScans(long ms) {
        setScanInterval(ms);
    }

    @Override
    public void setPort(int port) {
        try{
            Properties properties = new Properties();
             String path = PROP_FILE_PATH + "/application.properties";
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();

            properties.setProperty("clamav.port", String.valueOf(port));

            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void setHost(String host) {
        try{
            Properties properties = new Properties();
             String path = PROP_FILE_PATH + "/application.properties";
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();

            properties.setProperty("clamav.host", host);

            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void setScanInterval(long interval) {
        try {
            Properties properties = new Properties();
             String path = PROP_FILE_PATH + "/application.properties";
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();

            properties.setProperty("scan.interval", String.valueOf(interval));

            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, null);
            out.close();

            // Reschedule the scanning task with the new interval
            clamAVService.startScheduledTask();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }
}
