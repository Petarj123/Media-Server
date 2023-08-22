package com.petarj123.mediaserver.uploader.DTO;

import com.petarj123.mediaserver.uploader.file.model.FileType;
import lombok.Builder;

@Builder
public record Item(String id, String name, String path, FileType fileType) {
}
