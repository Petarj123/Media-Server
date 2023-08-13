package com.petarj123.mediaserver.uploader.file.service;

import com.petarj123.mediaserver.uploader.DTO.ScanResult;
import com.petarj123.mediaserver.uploader.exceptions.FileException;
import com.petarj123.mediaserver.uploader.exceptions.InfectedFileException;
import com.petarj123.mediaserver.uploader.file.model.File;
import com.petarj123.mediaserver.uploader.file.model.FileType;
import com.petarj123.mediaserver.uploader.file.model.SanitizedFile;
import com.petarj123.mediaserver.uploader.file.repository.FileRepository;
import com.petarj123.mediaserver.uploader.service.ClamAVService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileSystemService {

    protected final ClamAVService clamAVService;
    private final FileRepository fileRepository;
    @Value("${fileStorage.path}")
    private String serverFolderPath;

    protected void checkFileIsEmpty(MultipartFile multipartFile) throws FileException {
        if (multipartFile.isEmpty()) {
            throw new FileException("File is empty");
        }
    }

    protected Path prepareTemporaryPath(SanitizedFile sanitizedFileName) throws IOException {
        Path tempFolderPath = Paths.get(serverFolderPath, "temp");
        if (!Files.exists(tempFolderPath)) {
            Files.createDirectories(tempFolderPath);
        }
        return tempFolderPath.resolve(Objects.requireNonNull(sanitizedFileName.name));
    }

    protected void scanWithClamAV(Path path, MultipartFile multipartFile) throws InfectedFileException, IOException {
        ScanResult scanResult = clamAVService.scanFile(path);
        if (!scanResult.isClean()) {
            Files.delete(path);
            throw new InfectedFileException("File " + multipartFile.getOriginalFilename() + " is infected and has been deleted.");
        }
    }

    protected Path prepareFinalPath(SanitizedFile sanitizedFileName, String folderName) throws IOException, FileException {
        Path finalFolderPath = Paths.get(serverFolderPath, folderName);
        if (!Files.exists(finalFolderPath)) {
            Files.createDirectories(finalFolderPath);
        }
        Path finalTargetPath = finalFolderPath.resolve(Objects.requireNonNull(sanitizedFileName.name));
        if (Files.exists(finalTargetPath)) {
            throw new FileException("File " + sanitizedFileName.name + " already exists.");
        }
        return finalTargetPath;
    }

    protected Map<String, Object> extractFileMetadata(Path path, FileType fileType) throws EncoderException {
        Map<String, Object> metadata = new HashMap<>();
        if (fileType == FileType.VIDEO || fileType == FileType.PHOTO) {
            MultimediaObject multimediaObject = new MultimediaObject(path.toFile());
            MultimediaInfo multimediaInfo = multimediaObject.getInfo();
            metadata.put("duration", multimediaInfo.getDuration());
            metadata.put("format", multimediaInfo.getFormat());
            metadata.put("video", multimediaInfo.getVideo());
            metadata.put("audio", multimediaInfo.getAudio());
        }
        return metadata;
    }

    protected void buildAndSaveFile(SanitizedFile sanitizedFileName, Path finalTargetPath, FileType fileType, Map<String, Object> metadata) {
        Optional<File> existingFile = fileRepository.findByFileName(sanitizedFileName);
        int version = existingFile.map(file -> file.getVersion() + 1).orElse(1);
        File file = File.builder()
                .fileName(sanitizedFileName.name)
                .filePath(finalTargetPath.toString())
                .fileType(fileType)
                .version(version)
                .metadata(metadata)
                .build();
        fileRepository.save(file);
    }
}
