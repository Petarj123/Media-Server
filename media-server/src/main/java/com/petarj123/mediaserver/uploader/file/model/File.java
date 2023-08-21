package com.petarj123.mediaserver.uploader.file.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Document(collection = "files")
public class File {
    @Id
    private String id;
    private String fileName;
    private String filePath;
    private FileType fileType;
    private Integer version;
    private Map<String, Object> metadata;
    private String folderId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(fileName, file.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName);
    }
}
