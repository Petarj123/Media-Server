package com.petarj123.mediaserver.uploader.file.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class File {
    private String name;
    private Date createdAt;
}
