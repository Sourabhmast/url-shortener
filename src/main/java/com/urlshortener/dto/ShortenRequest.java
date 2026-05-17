package com.urlshortener.dto;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public class ShortenRequest {
    @NotEmpty(message = "URL cannot be empty")
    @URL(message = "Must be a valid HTTP/HTTPS URL")
    private String originalUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}
