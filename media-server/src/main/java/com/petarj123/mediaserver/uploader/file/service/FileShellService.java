package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import com.petarj123.mediaserver.uploader.file.model.Extension;
import com.petarj123.mediaserver.uploader.file.model.FileType;
import com.petarj123.mediaserver.uploader.file.repository.ExtensionRepository;
import com.petarj123.mediaserver.uploader.interfaces.FileShellServiceImpl;
import com.petarj123.mediaserver.uploader.service.DatabaseShellService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class FileShellService implements FileShellServiceImpl {
    @Value("${properties.path}")
    private String PROP_FILE_PATH;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseShellService.class);
    private final FileSanitizer fileSanitizer;
    private final ExtensionRepository extensionRepository;
    @Override
    public void setMaxFileSize(long mb) {
        try {
            Properties properties = new Properties();
            String path = PROP_FILE_PATH + "/application.properties";
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();

            String size = mb + "MB";
            properties.setProperty("spring.servlet.multipart.max-file-size", size);

            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void setMaxRequestSize(long mb) {
        try {
            Properties properties = new Properties();
            String path = PROP_FILE_PATH + "/application.properties";
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();

            String size = mb + "MB";
            properties.setProperty("spring.servlet.multipart.max-request-size", size);

            FileOutputStream out = new FileOutputStream(path);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void setAllowedExtensions(FileType fileType, List<String> extensions) throws InvalidFileExtensionException {
        Extension ext = extensionRepository.findByFileType(fileType).orElseThrow(() -> new InvalidFileExtensionException("Unsupported file type"));
        List<String> currentExtensions = ext.getExtension();
        currentExtensions.addAll(extensions);
        ext.setExtension(currentExtensions);
        extensionRepository.save(ext);
    }

    @Override
    public void removeExtension(FileType fileType, String extension) throws InvalidFileExtensionException {
        Extension ext = extensionRepository.findByFileType(fileType).orElseThrow(() -> new InvalidFileExtensionException("Unsupported file type"));
        List<String> currentExtensions = ext.getExtension();
        currentExtensions.removeIf(ex -> ex.equals(extension));
        ext.setExtension(currentExtensions);
        extensionRepository.save(ext);
    }
}
