package com.urlshortener.service;

import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.Url;
import com.urlshortener.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {
    
    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    
    private final UrlRepository urlRepository;
    private final Base62Service base62Service;

    public UrlService(UrlRepository urlRepository, Base62Service base62Service) {
        this.urlRepository = urlRepository;
        this.base62Service = base62Service;
    }

    @Transactional
    public Url shortenUrl(String originalUrl) {
        Url url = new Url(originalUrl);
        url = urlRepository.save(url);
        
        String shortCode = base62Service.encode(url.getId());
        url.setShortCode(shortCode);
        
        return urlRepository.save(url);
    }

    @Cacheable(value = "url", key = "#shortCode")
    @Transactional
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));
                
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
        
        return url.getOriginalUrl();
    }
    
    @Transactional(readOnly = true)
    public Url getUrlStats(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));
    }
}
