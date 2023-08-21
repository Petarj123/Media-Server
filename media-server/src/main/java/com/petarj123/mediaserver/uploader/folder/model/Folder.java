package com.petarj123.mediaserver.uploader.folder.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "folders")
public class Folder {
    @Id
    private String id;
    private String name;
    private String path;
    private String parentFolderId;
    private List<String> childFolderIds;
    private List<String> childFileIds;
    private Date createdAt;
    private Date modifiedAt;
}
