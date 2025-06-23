package com.example.clonemedium.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class BlogController {
    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);
    private final RestTemplate restTemplate;

    public BlogController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/welcome")
    public String getBlogs(@RequestParam(required = false) String search, Model model) {
        try {
            String apiUrl = "https://dev.to/api/articles";
            if (search != null && !search.trim().isEmpty()) {
                apiUrl += "?tag=" + search.trim();
            } else {
                apiUrl += "?top=10";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36");
            headers.set("Accept", "application/json");
            headers.set("Accept-Language", "en-US,en;q=0.9");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, List.class);
            List<Map<String, Object>> articles = response.getBody();
            
            if (articles != null) {
                articles = articles.stream()
                    .map(article -> {
                        try {
                            Object publishedAtObj = article.get("published_at");
                            if (publishedAtObj != null) {
                                String publishedAt = publishedAtObj.toString();
                                logger.debug("Original published_at: {}", publishedAt);
                                
                                ZonedDateTime dateTime;
                                try {
                                    dateTime = ZonedDateTime.parse(publishedAt);
                                } catch (DateTimeParseException e) {
                                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                                    dateTime = ZonedDateTime.parse(publishedAt, formatter);
                                }
                                
                                String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("MMM dd"));
                                logger.debug("Formatted date: {}", formattedDate);
                                article.put("published_at", formattedDate);
                            }
                        } catch (Exception e) {
                            logger.error("Error formatting date for article: {}", article.get("title"), e);
                            article.put("published_at", "N/A");
                        }
                        return article;
                    })
                    .collect(Collectors.toList());
            } else {
                articles = new ArrayList<>();
            }
            
            model.addAttribute("articles", articles);
            model.addAttribute("searchQuery", search);
        } catch (Exception e) {
            logger.error("Error fetching articles from Dev.to API", e);
            model.addAttribute("articles", new ArrayList<>());
            model.addAttribute("errorMessage", "Could not load articles. Please try again later.");
        }

        return "welcome";
    }
} 