package com.petarj123.mediaserver.uploader.folder.repository;

import com.petarj123.mediaserver.uploader.folder.model.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {
    Optional<Folder> findByName(String name);

    List<Folder> findAllByPathStartingWith(String pathPrefix);

    Optional<Folder> findByPath(String userFolderPath);

    List<Folder> findAllByIdIn(List<String> childFolderIds);
}
