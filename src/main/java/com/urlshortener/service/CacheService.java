package com.urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class CacheService {
    
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();

    public void logHit(String key) {
        long h = hits.incrementAndGet();
        log.debug("Cache HIT for key: {}. Total Hits: {}, Total Misses: {}", key, h, misses.get());
    }

    public void logMiss(String key) {
        long m = misses.incrementAndGet();
        log.debug("Cache MISS for key: {}. Total Hits: {}, Total Misses: {}", key, hits.get(), m);
    }
    
    public long getHits() {
        return hits.get();
    }
    
    public long getMisses() {
        return misses.get();
    }
}
