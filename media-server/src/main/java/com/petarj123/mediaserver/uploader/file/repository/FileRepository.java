package com.petarj123.mediaserver.uploader.file.repository;

import com.petarj123.mediaserver.uploader.file.model.File;
import com.petarj123.mediaserver.uploader.file.model.SanitizedFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
    Optional<File> findByFileName(SanitizedFile sanitizedFileName);
    Optional<File> findByFileName(String fileName);
    Optional<File> findByFileNameAndFolderId(String fileName, String folderId);
    long countByFileNameAndFilePath(String fileName, String filePath);

    List<File> findAllByIdIn(List<String> childFileIds);

    Optional<File> findByFileNameAndFilePath(String name, String path);
}
