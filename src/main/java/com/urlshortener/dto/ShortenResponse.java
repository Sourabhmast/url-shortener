package com.urlshortener.dto;

import java.time.LocalDateTime;

public class ShortenResponse {
    private String shortUrl;
    private String shortCode;
    private LocalDateTime createdAt;

    public ShortenResponse(String shortUrl, String shortCode, LocalDateTime createdAt) {
        this.shortUrl = shortUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
