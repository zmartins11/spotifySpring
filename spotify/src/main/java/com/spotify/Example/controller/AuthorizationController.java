package com.spotify.Example.controller;

import com.spotify.Example.KeysEnum;
import com.spotify.Example.service.SpotifyService;
import jakarta.servlet.http.HttpServletResponse;
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
        String a ="teste";
        code = userCode;
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();

        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            e.printStackTrace();
        }

        SavedTrack [] savedTracks = fetchSavedTracks();

        response.sendRedirect("http://localhost:4200/top-artists");
        spotifyService.saveTracks(savedTracks);
        return spotifyApi.getAccessToken();
    }

    private SavedTrack[] fetchSavedTracks() throws IOException, ParseException, SpotifyWebApiException {
        final GetUsersSavedTracksRequest getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                .build();

        final Paging<SavedTrack> savedTrackPaging = getUsersSavedTracksRequest.execute();
        return savedTrackPaging.getItems();
    }

    private Artist[] fetchUserTopArtists() {
        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
                .time_range("medium_term")
                .limit(10)
                .offset(5)
                .build();

        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();

            return artistPaging.getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Artist[0];
    }




}