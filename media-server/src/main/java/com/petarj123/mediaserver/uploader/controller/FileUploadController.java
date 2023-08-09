package com.petarj123.mediaserver.uploader.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileUploadController {

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }
}
