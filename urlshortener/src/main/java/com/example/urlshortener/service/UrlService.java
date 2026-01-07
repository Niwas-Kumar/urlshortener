package com.example.urlshortener.service;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.exception.UrlExpiredException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.util.Base62Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UrlService(UrlRepository repository,
                      RedisTemplate<String, String> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    private final UrlRepository repository;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    private String normalizeUrl(String longUrl) {
        if (!longUrl.startsWith("http://") && !longUrl.startsWith("https://")) {
            return "https://" + longUrl;
        }
        return longUrl;
    }

    public String createShortUrl(String longUrl){
        longUrl = normalizeUrl(longUrl);
      Url url = new Url();
      url.setLongUrl(longUrl);

      repository.save(url);

      url.setExpiryAt(LocalDateTime.now().plusHours(24));

      String shortCode = Base62Util.encode(url.getId());
      url.setShortCode(shortCode);

      repository.save(url);

      return "http://localhost:8081/r/" + shortCode;
  }

  public String getLongUrl(String shortCode){

        Url url = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found"));

      if(url.getExpiryAt() != null && url.getExpiryAt().isBefore(LocalDateTime.now())){
          throw new UrlExpiredException("url is expired");
      }

      url.setHitCount(url.getHitCount() + 1);
      repository.save(url);

      String cachedUrl = redisTemplate.opsForValue().get(shortCode);
      if(cachedUrl != null){
          return cachedUrl;
      }
      long ttlSeconds = java.time.Duration.between(
              LocalDateTime.now(),
              url.getExpiryAt()
      ).getSeconds();

      if (ttlSeconds > 0) {
          redisTemplate.opsForValue()
                  .set(shortCode, url.getLongUrl(), ttlSeconds, TimeUnit.SECONDS);
      }

      return url.getLongUrl();
  }

    public Url getUrlByShortCode(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));
    }

    private void validateUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL format: " + url);
        }
    }

}
