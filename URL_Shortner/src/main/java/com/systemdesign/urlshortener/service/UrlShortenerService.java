package com.systemdesign.urlshortener.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.systemdesign.urlshortener.model.UrlMapping;
import com.systemdesign.urlshortener.repository.UrlMappingRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UrlShortenerService {

    private final UrlMappingRepository repository;
    private final com.systemdesign.urlshortener.component.SnowflakeIdGenerator idGenerator;

    public UrlShortenerService(UrlMappingRepository repository,
            com.systemdesign.urlshortener.component.SnowflakeIdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    public UrlMapping shortenUrl(String longUrl) {
        log.info("Shortening URL: {}", longUrl);
        int attempts = 0;
        int maxRetries = 3;

        while (attempts < maxRetries) {
            // Distributed ID (Snowflake) + Base62 Encoding
            long uniqueId = idGenerator.nextId();
            String shortId = com.systemdesign.urlshortener.util.Base62Encoder.encode(uniqueId);

            UrlMapping mapping = new UrlMapping();
            mapping.setShortId(shortId);
            mapping.setLongUrl(longUrl);

            Calendar calendar = Calendar.getInstance();
            mapping.setCreatedAt(calendar.getTime());

            // Default Expiry: 365 Days
            calendar.add(Calendar.DAY_OF_YEAR, 365);
            mapping.setExpiresAt(calendar.getTime());

            try {
                return repository.save(mapping);
            } catch (DataIntegrityViolationException e) {
                // Collision happened (Primary Key duplicate)
                log.warn("Collision detected for ID: {}, retrying...", shortId);
                attempts++;
            }
        }
        throw new RuntimeException("Failed to generate unique ID after retries");
    }

    public Optional<String> getOriginalUrl(String shortId) {
        return repository.findByShortId(shortId)
                .filter(mapping -> {
                    if (mapping.getExpiresAt() == null)
                        return true;
                    // Check if EXPIRED (ExpiresAt is BEFORE Now)
                    if (mapping.getExpiresAt().before(new Date())) {
                        log.info("Lazy Expiration: URL {} expired at {}", shortId, mapping.getExpiresAt());
                        return false;
                    }
                    return true;
                })
                .map(UrlMapping::getLongUrl);
    }
}
