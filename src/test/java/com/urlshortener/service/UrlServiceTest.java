package com.urlshortener.service;

import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.Url;
import com.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private Base62Service base62Service;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShortenUrl() {
        String originalUrl = "https://example.com";
        Url savedUrl = new Url(originalUrl);
        savedUrl.setId(1L);

        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);
        when(base62Service.encode(1L)).thenReturn("000001");

        Url result = urlService.shortenUrl(originalUrl);

        assertEquals("000001", result.getShortCode());
        assertEquals(originalUrl, result.getOriginalUrl());
    }

    @Test
    void testGetOriginalUrl_Found() {
        String shortCode = "000001";
        Url url = new Url("https://example.com");
        url.setShortCode(shortCode);
        url.setClickCount(0);

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(url));
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        String result = urlService.getOriginalUrl(shortCode);

        assertEquals("https://example.com", result);
        assertEquals(1, url.getClickCount());
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        when(urlRepository.findByShortCode("invalid")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl("invalid"));
    }
}
