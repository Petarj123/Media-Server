package com.petarj123.mediaserver.uploader.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UploaderExceptionHandler {
    // TODO Make DTOs for exceptions
    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleFileException(FileException ex) {
        return ex.getMessage();
    }
    @ExceptionHandler(FolderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleFolderException(FolderException ex) {
        return ex.getMessage();
    }
}
