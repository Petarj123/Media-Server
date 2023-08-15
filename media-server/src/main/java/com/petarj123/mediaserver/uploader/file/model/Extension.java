package com.petarj123.mediaserver.uploader.file.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "extensions")
@Builder
public class Extension {
    @Id
    private String id;
    private FileType fileType;
    private List<String> extension;

}
