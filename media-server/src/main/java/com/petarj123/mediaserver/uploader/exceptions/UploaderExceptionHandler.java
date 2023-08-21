package com.petarj123.mediaserver.uploader.exceptions;

import com.petarj123.mediaserver.uploader.DTO.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ControllerAdvice
public class UploaderExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> exceptionStatusMapping = Map.of(
            FileException.class, HttpStatus.BAD_REQUEST,
            FolderException.class, HttpStatus.BAD_REQUEST,
            InfectedFileException.class, HttpStatus.UNPROCESSABLE_ENTITY,
            InvalidFileExtensionException.class, HttpStatus.UNPROCESSABLE_ENTITY
    );

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse handleFileException(FileException ex) {
        HttpStatus status = exceptionStatusMapping.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildExceptionResponse(ex.getClass(), ex.getMessage(), status);
    }
    @ExceptionHandler(FolderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse handleFolderException(FolderException ex) {
         HttpStatus status = exceptionStatusMapping.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
         return buildExceptionResponse(ex.getClass(), ex.getMessage(), status);
    }
    @ExceptionHandler(InfectedFileException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ExceptionResponse handleInfectedFileException(InfectedFileException ex) {
        HttpStatus status = exceptionStatusMapping.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildExceptionResponse(ex.getClass(), ex.getMessage(), status);
    }
    @ExceptionHandler(InvalidFileExtensionException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ExceptionResponse handleInvalidFileExtensionException(InvalidFileExtensionException ex) {
        HttpStatus status = exceptionStatusMapping.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildExceptionResponse(ex.getClass(), ex.getMessage(), status);
    }

    private ExceptionResponse buildExceptionResponse(Class<? extends Exception> exceptionClass, String message, HttpStatus status) {
        return ExceptionResponse.builder()
                .exception(exceptionClass.getSimpleName())
                .message(message)
                .code(status.value())
                .build();
    }
}
