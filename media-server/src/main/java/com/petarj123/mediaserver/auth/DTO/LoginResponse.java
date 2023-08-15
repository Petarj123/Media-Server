package com.petarj123.mediaserver.auth.DTO;

import lombok.Builder;

@Builder
public record LoginResponse(String token) {
}
