package com.petarj123.mediaserver.uploader.service;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;

@Service
public class ClamAVService {

    @Value("${clamav.host}")
    private String clamAVHost;

    @Value("${clamav.port}")
    private int clamAVPort;


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

    // TODO Sanitize File Names, File Size Limit, Scheduled Scans, Retry Mechanism, Asynchronous Scanning, Batch Scanning

}
