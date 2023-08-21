package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import com.petarj123.mediaserver.uploader.file.model.FileType;
import com.petarj123.mediaserver.uploader.file.model.SanitizedFile;
import com.petarj123.mediaserver.uploader.file.repository.ExtensionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileSanitizer {

    private final ExtensionRepository extensionRepository;

    private List<String> photoExtensions;
    private List<String> videoExtensions;
    private List<String> textExtensions;
    private List<String> pdfExtensions;

    @PostConstruct
    public void init() {
        photoExtensions = extensionRepository.findByFileType(FileType.PHOTO).orElseThrow().getExtension();
        videoExtensions = extensionRepository.findByFileType(FileType.VIDEO).orElseThrow().getExtension();
        textExtensions = extensionRepository.findByFileType(FileType.TEXT).orElseThrow().getExtension();
        pdfExtensions = extensionRepository.findByFileType(FileType.PDF).orElseThrow().getExtension();
    }


    // TODO Vidi sta treba da se desi ako fajlovi imaju isto ime, ali drugaciji sadrzaj.
    protected SanitizedFile sanitizeFileName(String originalFilename) throws InvalidFileExtensionException {
        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Collapse multiple underscores into one
        sanitized = sanitized.replaceAll("_{2,}", "_");

        // Ensure file names don't start or end with a dot or underscore
        sanitized = sanitized.replaceAll("^[._]", "");
        sanitized = sanitized.replaceAll("[._]$", "");

        // Ensure file names don't contain sequences like ".."
        sanitized = sanitized.replace("..", ".");

        FileType fileType = determineFileType(sanitized);

        // Length limitation
        int MAX_FILENAME_LENGTH = 255;
        if (sanitized.length() > MAX_FILENAME_LENGTH) {
            throw new IllegalArgumentException("File name is too long");
        }

        return new SanitizedFile(sanitized, fileType);
    }
    private FileType determineFileType(String filename) throws InvalidFileExtensionException {
        filename = filename.toLowerCase();
        if (checkExtensionInList(filename, photoExtensions)) {
            return FileType.PHOTO;
        }
        if (checkExtensionInList(filename, videoExtensions)) {
            return FileType.VIDEO;
        }
        if (checkExtensionInList(filename, textExtensions)) {
            return FileType.TEXT;
        }
        if (checkExtensionInList(filename, pdfExtensions)) {
            return FileType.PDF;
        }

        throw new InvalidFileExtensionException("Unsupported file extension: " + getExtension(filename));
    }
    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
    private boolean checkExtensionInList(String filename, List<String> extensions){
        for (String ext : extensions){
            if (filename.endsWith(ext)){
                return true;
            }
        }
        return false;
    }
}
