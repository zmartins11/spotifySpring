package com.spotify.Example.controller;

import com.spotify.Example.KeysEnum;
import com.spotify.Example.service.ApiService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;

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
