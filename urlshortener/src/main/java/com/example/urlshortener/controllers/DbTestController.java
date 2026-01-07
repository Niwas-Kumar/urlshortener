package com.example.urlshortener.controllers;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbTestController {

    @Autowired
    private UrlRepository repository;

    @GetMapping("/db-test")
    public String testDb(){
        Url url = new Url();
        url.setLongUrl("www.google.com");
        repository.save(url);
        return "Save with Id :" + url.getId();
    }
}
