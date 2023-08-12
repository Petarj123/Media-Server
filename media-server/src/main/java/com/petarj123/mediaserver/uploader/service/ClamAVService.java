package com.petarj123.mediaserver.uploader.service;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.InfectedFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class ClamAVService {

    @Value("${clamav.host}")
    private String clamAVHost;

    @Value("${clamav.port}")
    private int clamAVPort;

    private static final String serverFolderPath = "/home/petarjankovic/Documents/Server/";
    private static final Logger logger = LoggerFactory.getLogger(ClamAVService.class);

    public ScanResult scanFile(Path file) throws IOException {
        String responseString;
        try (Socket socket = new Socket(clamAVHost, clamAVPort);
             OutputStream os = socket.getOutputStream();
             InputStream is = socket.getInputStream()) {

            os.write(("SCAN " + file.toString() + "\n").getBytes());
            os.flush();

            byte[] buffer = new byte[2048];
            int bytesRead;
            StringBuilder response = new StringBuilder();
            while ((bytesRead = is.read(buffer)) > 0) {
                response.append(new String(buffer, 0, bytesRead));
            }
            responseString = response.toString();
        }

        boolean isClean = !responseString.contains("FOUND");
        return new ScanResult(file.toString(), isClean);
    }

    // TODO Sanitize File Names, File Size Limit, Retry Mechanism, Asynchronous Scanning, Batch Scanning
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void scanServer() {
        try (Stream<Path> paths = Files.walk(Paths.get(serverFolderPath))) {
            paths
                    .filter(Files::isRegularFile) // Only scan files, not directories
                    .forEach(file -> {
                        try {
                            ScanResult scanResult = scanFile(file);
                            if (!scanResult.isClean()) {
                                Files.delete(file);
                                throw new InfectedFileException("File " + file.getFileName() + " is infected and has been deleted.");
                            }
                        } catch (IOException e) {
                            logger.error("Error scanning or deleting file: {}", file.getFileName(), e);
                        }
                    });
        } catch (IOException e) {
            logger.error("Error walking through server folder path", e);
        }
    }

}
