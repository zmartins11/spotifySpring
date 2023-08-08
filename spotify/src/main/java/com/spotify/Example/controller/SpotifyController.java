package com.spotify.Example.controller;

import com.spotify.Example.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SpotifyController {

private final ApiService apiService;


    @Autowired
    public SpotifyController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("testFlask")
    public ResponseEntity<String> testFlask() {
        return apiService.callFlaskApi();
    }

}
