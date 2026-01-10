package com.systemdesign.urlshortener.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.systemdesign.urlshortener.model.UrlMapping;


@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {

    @Cacheable(value = "urls", key = "#p0")
    Optional<UrlMapping> findByShortId(String shortId);

    @Modifying
    @Transactional
    void deleteByExpiresAtBefore(Date date);
}
