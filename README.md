# URL Shortener 📝🔗

![Java](https://img.shields.io/badge/Java-22-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue) ![Redis](https://img.shields.io/badge/Redis-7.0-orange) ![License](https://img.shields.io/badge/License-MIT-lightgrey)

A full-stack **URL Shortener** built with **Spring Boot**, **MySQL**, **Redis**, and a lightweight **HTML/JS frontend**. Shorten long URLs, track hits, handle expiry, and redirect users efficiently.  

---

## 🚀 Features

- Shorten long URLs into compact, easy-to-share links.  
- Click tracking / hit count for each URL.  
- Expiry for short URLs (24 hours default).  
- Automatic redirect when visiting a short URL.  
- Redis caching for faster URL resolution.  
- Global exception handling with user-friendly messages.  
- Simple HTML frontend with copy-to-clipboard functionality.  

---

## 🛠️ Technologies Used

| Layer          | Technology                   |
|----------------|------------------------------|
| Backend        | Java 22, Spring Boot 4.0.1   |
| Database       | MySQL / H2 (for testing)     |
| Cache          | Redis                        |
| Frontend       | HTML, CSS, JavaScript        |
| Build Tool     | Maven                        |

---

## 🏗️ Project Structure

```text
UrlShortener
├── src/main/java/com/example/urlshortener
│   ├── controllers
│   │   └── UrlController.java
│   ├── service
│   │   └── UrlService.java
│   ├── repository
│   │   └── UrlRepository.java
│   ├── entity
│   │   └── Url.java
│   └── exception
│       ├── UrlNotFoundException.java
│       └── UrlExpiredException.java
├── src/main/resources
│   ├── application.properties
│   └── static/index.html
└── pom.xml
