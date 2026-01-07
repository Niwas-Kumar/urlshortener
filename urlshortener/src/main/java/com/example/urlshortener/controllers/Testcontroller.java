package com.example.urlshortener.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testcontroller {

    @GetMapping("/test")
     public String test() {
         return "Url shortener is running....";
    }
}
