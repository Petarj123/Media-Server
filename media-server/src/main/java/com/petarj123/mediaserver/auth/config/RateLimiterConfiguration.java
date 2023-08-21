package com.petarj123.mediaserver.auth.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@RequiredArgsConstructor
public class RateLimiterConfiguration {

    private final RateLimiterRegistry rateLimiterRegistry;

    @Bean
    public RateLimiter authRateLimiter() {
        RateLimiterConfig auth = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.of(10, ChronoUnit.SECONDS))
                .timeoutDuration(Duration.of(5, ChronoUnit.SECONDS))
                .build();
        return rateLimiterRegistry.rateLimiter("auth", auth);
    }
    @Bean
    public RateLimiter fileAndFolderRateLimiter() {
        RateLimiterConfig fileAndFolder = RateLimiterConfig.custom()
                .limitForPeriod(60)
                .limitRefreshPeriod(Duration.of(30, ChronoUnit.SECONDS))
                .timeoutDuration(Duration.of(5, ChronoUnit.SECONDS))
                .build();
        return rateLimiterRegistry.rateLimiter("fileAndFolder", fileAndFolder);
    }
    @Bean
    public RateLimiter frontendLimiter() {
        RateLimiterConfig frontend = RateLimiterConfig.custom()
                .limitForPeriod(60)
                .limitRefreshPeriod(Duration.of(30, ChronoUnit.SECONDS))
                .timeoutDuration(Duration.of(5, ChronoUnit.SECONDS))
                .build();
        return rateLimiterRegistry.rateLimiter("frontend", frontend);
    }
}
