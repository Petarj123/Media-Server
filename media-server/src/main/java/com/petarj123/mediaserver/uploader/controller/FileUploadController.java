package com.petarj123.mediaserver.uploader.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileUploadController {

    @GetMapping("/home")
    @RateLimiter(name = "frontend")
    public String showUploadForm() {
        return "upload";
    }
    @GetMapping("/")
    @RateLimiter(name = "frontend")
    public String getLoginForm() {
        return "login";
    }
    @GetMapping("/register")
    @RateLimiter(name = "frontend")
    public String getRegisterForm() {
        return "register";
    }
}
