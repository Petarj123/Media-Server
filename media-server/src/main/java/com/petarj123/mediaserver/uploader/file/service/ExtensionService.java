package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.file.model.Extension;
import com.petarj123.mediaserver.uploader.file.model.FileType;
import com.petarj123.mediaserver.uploader.file.repository.ExtensionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ExtensionService {

    private final ExtensionRepository extensionRepository;

    @PostConstruct
    public void init() {
        if (extensionRepository.findByFileType(FileType.PHOTO).isEmpty()) {
            extensionRepository.save(Extension.builder().fileType(FileType.PHOTO).extension(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp")).build());
        }
        if (extensionRepository.findByFileType(FileType.VIDEO).isEmpty()) {
            extensionRepository.save(Extension.builder().fileType(FileType.VIDEO).extension(Arrays.asList(".mp4", ".avi", ".mkv", ".mov", ".flv")).build());
        }
        if (extensionRepository.findByFileType(FileType.TEXT).isEmpty()) {
            extensionRepository.save(Extension.builder().fileType(FileType.TEXT).extension(Arrays.asList(".txt", ".md", ".doc", ".docx")).build());
        }
        if (extensionRepository.findByFileType(FileType.PDF).isEmpty()) {
            extensionRepository.save(Extension.builder().fileType(FileType.PDF).extension(Arrays.asList(".pdf")).build());
        }
    }
}
