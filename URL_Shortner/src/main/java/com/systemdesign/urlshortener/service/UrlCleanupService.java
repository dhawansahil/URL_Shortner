package com.systemdesign.urlshortener.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.systemdesign.urlshortener.repository.UrlMappingRepository;

@Service
public class UrlCleanupService {

    private final UrlMappingRepository repository;

    public UrlCleanupService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    /**
     * Triggered by External API / Batch Job.
     */
    public void removeExpiredUrls() {
        Date now = new Date();
        try {
            repository.deleteByExpiresAtBefore(now);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
