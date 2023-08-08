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

    private static final URI redirecUri = SpotifyHttpManager.makeUri("http://localhost:8080/api/get-user-code/");

private final ApiService apiService;


    private String code = "";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(KeysEnum.CLIENT_ID.getKey())
            .setClientSecret(KeysEnum.CLIENT_SECRET.getKey())
            .setRedirectUri(redirecUri)
            .build();

    @Autowired
    public SpotifyController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("loginTeste")
    @ResponseBody
    public String spotifyLogin() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-read-private, user-read-email, user-top-read")
                .show_dialog(true)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    @GetMapping("testFlask")
    public ResponseEntity<String> testFlask() {
        return apiService.callFlaskApi();
    }

    @GetMapping(value = "get-user-code")
    public String getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        code = userCode;
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in:" + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {

        }

        response.sendRedirect("http://localhost:4200/top-artists");
        return spotifyApi.getAccessToken();
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code) {
        String accessTokenUrl = "https://accounts.spotify.com/api/token";

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", "http://localhost:8080/callback"); // Replace with your actual redirect URI
        body.add("client_id", "6b64dd17fbeb44c7aa8eea3fadc6c28f"); // Replace with your actual client ID
        body.add("client_secret", "17631b9e74f045b29eb245236daf399b"); // Replace with your actual client secret

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                accessTokenUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        // Parse the response and retrieve the access token
        String accessToken = response.getBody().split("\"access_token\":\"")[1].split("\"")[0];

        return "Your access token: " + accessToken;
    }
}
