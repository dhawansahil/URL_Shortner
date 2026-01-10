package com.systemdesign.urlshortener.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.systemdesign.urlshortener.dto.CreateUrlRequest;
import com.systemdesign.urlshortener.dto.CreateUrlResponse;
import com.systemdesign.urlshortener.model.UrlMapping;
import com.systemdesign.urlshortener.service.UrlCleanupService;
import com.systemdesign.urlshortener.service.UrlShortenerService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UrlShortenerController {

	@Autowired
	private UrlShortenerService service;

	@Autowired
	private UrlCleanupService cleanupService;

	// In Lambda/CloudFront, we might want to configure the domain dynamically
	@Value("${app.domain:https://d123.cloudfront.net}")
	private String domain;

	@PostMapping("/api/v1/urls")
	public ResponseEntity<CreateUrlResponse> shortenUrl(@Valid @RequestBody CreateUrlRequest request) {
		log.info("Received request to shorten URL: {}", request.getLongUrl());
		UrlMapping mapping = service.shortenUrl(request.getLongUrl());
		String fullShortUrl = domain + "/" + mapping.getShortId();
		return ResponseEntity.ok(new CreateUrlResponse(fullShortUrl, mapping.getShortId()));
	}

	@GetMapping("/{shortId}")
	public ResponseEntity<Void> redirect(@PathVariable String shortId) {
		log.info("Received redirect request for ID: {}", shortId);
		Optional<String> longUrl = service.getOriginalUrl(shortId);

		if (longUrl.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.status(HttpStatus.FOUND) // 302 Found (or 301 Moved Permanently)
				.location(URI.create(longUrl.get())).header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400") // Cache
																												// at
																												// CloudFront
																												// for
																												// 24h
				.build();
	}

	// Manual Endpoint for Cleanup (Internal Use)
	@PostMapping("/api/v1/internal/cleanup")
	public ResponseEntity<String> triggerCleanup() {
		cleanupService.removeExpiredUrls();
		return ResponseEntity.ok("Cleanup Triggered Successfully");
	}
}
