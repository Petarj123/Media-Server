package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.exceptions.InvalidFileExtensionException;
import com.petarj123.mediaserver.uploader.file.model.FileType;
import com.petarj123.mediaserver.uploader.file.model.SanitizedFile;
import org.springframework.stereotype.Component;

@Component
public class FileSanitizer {
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
        String[] photoExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        String[] videoExtensions = {".mp4", ".avi", ".mkv", ".mov", ".flv"};
        String[] textExtensions = {".txt", ".md", ".doc", ".docx"};
        String[] pdfExtensions = {".pdf"};
        filename = filename.toLowerCase();
        for (String ext : photoExtensions) {
            if (filename.endsWith(ext)) {
                return FileType.PHOTO;
            }
        }

        for (String ext : videoExtensions) {
            if (filename.endsWith(ext)) {
                return FileType.VIDEO;
            }
        }

        for (String ext : textExtensions) {
            if (filename.endsWith(ext)) {
                return FileType.TEXT;
            }
        }

        for (String ext : pdfExtensions) {
            if (filename.endsWith(ext)) {
                return FileType.PDF;
            }
        }

        throw new InvalidFileExtensionException("Unsupported file extension: " + getExtension(filename));
    }
    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
}
