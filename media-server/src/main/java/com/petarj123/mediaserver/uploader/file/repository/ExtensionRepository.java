package com.petarj123.mediaserver.uploader.file.repository;

import com.petarj123.mediaserver.uploader.file.model.Extension;
import com.petarj123.mediaserver.uploader.file.model.FileType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExtensionRepository extends MongoRepository<Extension, String> {
    Optional<Extension> findByFileType(FileType fileType);

}
