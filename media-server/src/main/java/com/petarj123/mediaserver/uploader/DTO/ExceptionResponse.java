package com.petarj123.mediaserver.uploader.DTO;

import lombok.Builder;

@Builder
public record ExceptionResponse(String exception, String message, int code) {
}
