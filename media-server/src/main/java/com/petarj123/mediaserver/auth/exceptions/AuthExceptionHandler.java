package com.petarj123.mediaserver.auth.exceptions;

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
    // TODO Handle other exceptions
    @ExceptionHandler(RequestNotPermitted.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ResponseBody
    public Map<String, String> handleRateLimitExceededException(RequestNotPermitted e) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", "Too many requests");
        errorDetails.put("message", "You have exceeded the rate limit.");
        return errorDetails;
    }
}
