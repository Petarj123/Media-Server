package com.petarj123.mediaserver.uploader.folder.repository;

import com.petarj123.mediaserver.uploader.folder.model.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {
}
