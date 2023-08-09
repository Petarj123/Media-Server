package com.petarj123.mediaserver.uploader.folder.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Folder {
    private String name;
    private Date createdAt;
}
