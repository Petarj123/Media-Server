package com.petarj123.mediaserver.uploader.interfaces;

public interface FileShellServiceImpl {
    void setMaxFileSize(long size);

    void setAllowedExtensions(String... extensions);
}
