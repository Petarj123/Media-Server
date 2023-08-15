package com.petarj123.mediaserver.uploader.interfaces;

import java.time.LocalDateTime;

public interface ClamAVShellServiceImpl {
    void enableClamAV();

    void disableClamAV();

    void enableScans();

    void disableScans();

    void scheduleScans(long ms);

    void setPort(int port);

    void setHost(String host);
}
