package com.petarj123.mediaserver.uploader.service;

import com.petarj123.mediaserver.uploader.interfaces.ClamAVShellServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClamAVShellService implements ClamAVShellServiceImpl {
    @Override
    public void enableClamAV() {

    }

    @Override
    public void disableClamAV() {

    }

    @Override
    public void enableScans() {

    }

    @Override
    public void disableScans() {

    }

    @Override
    public void scheduleScans(LocalDateTime time) {

    }

    @Override
    public void setPort(int port) {

    }

    @Override
    public void setHost(String host) {

    }
}
