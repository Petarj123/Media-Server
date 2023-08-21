package com.petarj123.mediaserver.auth.exceptions;

import com.petarj123.mediaserver.uploader.DTO.ExceptionResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AuthExceptionHandler {
    private static final Map<Class<? extends Exception>, HttpStatus> exceptionStatusMapping = Map.of(
            EmailExistsException.class, HttpStatus.CONFLICT,
            InvalidEmailException.class, HttpStatus.BAD_REQUEST,
            InvalidPasswordException.class, HttpStatus.BAD_REQUEST,
            PasswordMismatchException.class, HttpStatus.UNAUTHORIZED,
            UsernameExistsException.class, HttpStatus.CONFLICT
    );
    @ExceptionHandler(RequestNotPermitted.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ResponseBody
    public Map<String, String> handleRateLimitExceededException(RequestNotPermitted e) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", "Too many requests");
        errorDetails.put("message", "You have exceeded the rate limit.");
        return errorDetails;
    }
    @ExceptionHandler(EmailExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ExceptionResponse handleEmailExistsException(EmailExistsException ex) {
        HttpStatus status = exceptionStatusMapping.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildExceptionResponse(ex.getClass(), ex.getMessage(), status);
    }

    @ExceptionHandler(InvalidEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse handleInvalidEmailException(InvalidEmailException ex) {
        HttpStatus status = exceptionStatusMapping.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildExceptionResponse(ex.getClass(), ex.getMessage(), status);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse handleInvalidPasswordException(InvalidPasswordException ex) {
        HttpStatus status = exceptionStatusMapping.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildExceptionResponse(ex.getClass(), ex.getMessage(), status);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ExceptionResponse handlePasswordMismatchException(PasswordMismatchException ex) {
        HttpStatus status = exceptionStatusMapping.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildExceptionResponse(ex.getClass(), ex.getMessage(), status);
    }

    @ExceptionHandler(UsernameExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ExceptionResponse handleUsernameExistsException(UsernameExistsException ex) {
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
