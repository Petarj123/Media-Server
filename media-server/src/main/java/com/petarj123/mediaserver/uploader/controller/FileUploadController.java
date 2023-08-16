package com.petarj123.mediaserver.uploader.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileUploadController {

    @GetMapping("/home")
    public String showUploadForm() {
        return "upload";
    }
    @GetMapping("/")
    public String getLoginForm() {
        return "login";
    }
    @GetMapping("/register")
    public String getRegisterForm() {
        return "register";
    }
}
