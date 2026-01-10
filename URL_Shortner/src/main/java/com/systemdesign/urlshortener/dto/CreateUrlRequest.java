package com.systemdesign.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

import org.hibernate.validator.constraints.URL;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUrlRequest implements Serializable{
   
	private static final long serialVersionUID = 1L;
	@NotBlank(message = "URL cannot be empty")
    @URL(message = "Invalid URL format")
    private String longUrl;
}
