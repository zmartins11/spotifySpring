package com.spotify.Example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiService {

    private final RestTemplate restTemplate;

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> callFlaskApi() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> data = new HashMap<>();
        data.put("foo fighters", "pretender");
        data.put("derek trucks", "crow jane");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(data, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://127.0.0.1:5000/testSpring", requestEntity, String.class);
        return response;
    }
}
