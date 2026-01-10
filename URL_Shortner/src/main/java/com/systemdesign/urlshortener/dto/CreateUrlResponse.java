package com.systemdesign.urlshortener.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class CreateUrlResponse {
    private String shortUrl;
    private String code;
}
