package com.spotify.Example.controller;

import com.spotify.Example.enums.KeysEnum;
import com.spotify.Example.service.SpotifyService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class AuthorizationController {

    private static final URI redirectURi = SpotifyHttpManager.makeUri("http://localhost:8080/get-user-code");
    private String code = "";

    private final SpotifyService spotifyService;

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(KeysEnum.CLIENT_ID.getKey())
            .setClientSecret(KeysEnum.CLIENT_SECRET.getKey())
            .setRedirectUri(redirectURi)
            .build();

    @Autowired
    public AuthorizationController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("login")
    @ResponseBody
    public String spotifyLogin() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-library-read, user-top-read, user-read-private, user-read-email")
                .show_dialog(true)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    @GetMapping("/get-user-code")
    public String getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException, ParseException, SpotifyWebApiException {
        code = userCode;
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();

        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            spotifyService.storeSession(spotifyApi, null);

        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            e.printStackTrace();
        }

        try {
            SavedTrack [] savedTracks = spotifyService.getUserTracks(spotifyApi);
//            spotifyService.saveTracks(savedTracks);
        } catch (Exception e) {
            System.out.println("error token expired");
        }

        response.sendRedirect("http://localhost:4200/top-artists");

        return spotifyApi.getAccessToken();
    }


    @GetMapping("test")
    @ResponseBody
    public String testDocker() {
        return "testing docker";
    }




}
