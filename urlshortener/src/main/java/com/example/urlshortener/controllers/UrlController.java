package com.example.urlshortener.controllers;

import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;


@RestController
public class UrlController {

    private final UrlService urlservice;

    public UrlController(UrlService urlservice) {
        this.urlservice = urlservice;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> createShortUrl(@RequestBody UrlRequest request){
        String shortUrl = urlservice.createShortUrl(request.getLongUrl());

        return ResponseEntity.ok(
                Map.of("shortUrl", shortUrl)
        );
    }

    @GetMapping("/r/{shortCode}")
    public void redirect( @PathVariable String shortCode,
                           HttpServletResponse response) throws IOException {
                    String longUrl = urlservice.getLongUrl(shortCode);
                    response.sendRedirect(longUrl);
        }

    @GetMapping("/analytics/{shortCode}")
    public ResponseEntity<?> getAnalytics(@PathVariable String shortCode) {
        Url url = urlservice.getUrlByShortCode(shortCode);
        return ResponseEntity.ok(
                Map.of(
                        "shortCode", shortCode,
                        "longUrl", url.getLongUrl(),
                        "hitCount", url.getHitCount(),
                        "expiryAt", url.getExpiryAt()
                )
        );
    }

}

