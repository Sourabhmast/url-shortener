package com.urlshortener.config;

import com.urlshortener.service.CacheService;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

public class LoggingCache implements Cache {

    private final Cache delegate;
    private final CacheService cacheService;

    public LoggingCache(Cache delegate, CacheService cacheService) {
        this.delegate = delegate;
        this.cacheService = cacheService;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    @Nullable
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = delegate.get(key);
        if (valueWrapper != null) {
            cacheService.logHit(getName() + ":" + key);
        } else {
            cacheService.logMiss(getName() + ":" + key);
        }
        return valueWrapper;
    }

    @Override
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        T value = delegate.get(key, type);
        if (value != null) {
            cacheService.logHit(getName() + ":" + key);
        } else {
            cacheService.logMiss(getName() + ":" + key);
        }
        return value;
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        // Simple approximation for valueLoader
        ValueWrapper valueWrapper = delegate.get(key);
        if (valueWrapper != null) {
            cacheService.logHit(getName() + ":" + key);
            return (T) valueWrapper.get();
        } else {
            cacheService.logMiss(getName() + ":" + key);
            return delegate.get(key, valueLoader);
        }
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        delegate.put(key, value);
    }

    @Override
    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        delegate.evict(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return delegate.evictIfPresent(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean invalidate() {
        return delegate.invalidate();
    }
}
