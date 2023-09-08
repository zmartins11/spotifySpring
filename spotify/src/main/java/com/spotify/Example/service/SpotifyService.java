package com.spotify.Example.service;

import com.spotify.Example.model.SavedTrackEntity;
import com.spotify.Example.model.SessionEntity;
import com.spotify.Example.repository.SavedTrackRepository;
import com.spotify.Example.repository.SessionRepository;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpotifyService {

    SavedTrackRepository savedTrackRepository;
    SessionRepository sessionRepository;

    @Autowired
    public SpotifyService(SavedTrackRepository savedTrackRepository, SessionRepository sessionRepository) {
        this.savedTrackRepository = savedTrackRepository;
        this.sessionRepository = sessionRepository;
    }

    public SavedTrack[] getUserTracks(SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException {
        int offset = 0;
        List<SavedTrack> allSavedTracks = new ArrayList<>();

        while(true) {
            final GetUsersSavedTracksRequest getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                    .offset(offset)
                    .build();

            final Paging<SavedTrack> savedTrackPaging = getUsersSavedTracksRequest.execute();
            SavedTrack[] savedTracks = savedTrackPaging.getItems();

            if(savedTracks.length == 0) {
                break;
            }

            allSavedTracks.addAll(Arrays.asList(savedTracks));
            offset += savedTracks.length;
        }
        return allSavedTracks.toArray(new SavedTrack[0]);
    }


    public int getNumberOfSavedTracks(SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException {
        int offset = 0;
        int totalCount = 0;

        while (true) {
            final GetUsersSavedTracksRequest getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                    .offset(offset)
                    .build();
        try {
            final Paging<SavedTrack> savedTrackPaging = getUsersSavedTracksRequest.execute();
            SavedTrack[] savedTracks = savedTrackPaging.getItems();

            if (savedTracks.length == 0) {
                break;
            }

            totalCount += savedTracks.length;
            offset += savedTracks.length;
        } catch (Exception e) {
            //token expired
            break;
            }
        }

        return totalCount;
    }

//    public int getNumberDbTracks() {
//
//    }


    public void saveTracks(SavedTrack[] savedTracks) {
        for(SavedTrack track : savedTracks) {
            String song = track.getTrack().getName();
            String firstArtist = Arrays.stream(track.getTrack().getArtists())
                    .findFirst()
                    .map(ArtistSimplified::getName)
                    .orElse("");

            SavedTrackEntity trackEntity = new SavedTrackEntity();
            trackEntity.setSongName(song);
            trackEntity.setArtist(firstArtist);

            savedTrackRepository.save(trackEntity);
        }
    }

    private Artist[] fetchUserTopArtists(SpotifyApi spotifyApi) {
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


    public void storeSession(SpotifyApi spotifyApi, String initialRefreshToken) {
        SessionEntity sessionEntity = new SessionEntity();
        long currentTimestampMillis = System.currentTimeMillis() / 1000;

        sessionRepository.deleteAll();

        sessionEntity.setAccessToken(spotifyApi.getAccessToken());
        if(spotifyApi.getRefreshToken() != null && !spotifyApi.getRefreshToken().isEmpty()) {
            sessionEntity.setRefreshToken(spotifyApi.getRefreshToken());
        } else {
            sessionEntity.setRefreshToken(initialRefreshToken);
        }
        sessionEntity.setTimestamp(currentTimestampMillis);

        sessionRepository.save(sessionEntity);
    }

    public SessionEntity getSession() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        SessionEntity session  = (SessionEntity) sessionRepository.findAll(sort).stream().findFirst().orElse(null);
        return session;
    }

    public SpotifyApi handleTokenExpired(SpotifyApi spotifyApi, Long tokenTime, String initialRefreshToken) throws IOException, ParseException, SpotifyWebApiException {

        if(isAccessTokenAboutToExpire(spotifyApi, tokenTime)) {
            AuthorizationCodeRefreshRequest refreshRequest = spotifyApi.authorizationCodeRefresh()
                    .refresh_token(spotifyApi.getRefreshToken())
                    .build();

            try {
                final AuthorizationCodeCredentials credentials = refreshRequest.execute();


                spotifyApi.setAccessToken(credentials.getAccessToken());
                spotifyApi.setRefreshToken(credentials.getRefreshToken());

                storeSession(spotifyApi, initialRefreshToken);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return spotifyApi;
    }

    private boolean isAccessTokenAboutToExpire(SpotifyApi spotifyApi, Long tokenTime) {
        // Define a threshold (e.g., 5 minutes) before the access token expiration time
        long thresholdInSeconds = 300; // 5 minutes
        long currentTimestampInSeconds = System.currentTimeMillis() / 1000;
        long expirationTimestampInSeconds = tokenTime;

        // Check if the access token will expire within the threshold
        return (expirationTimestampInSeconds - currentTimestampInSeconds) <= thresholdInSeconds;
    }

    public int getNumberDbTracks() {
        return (int) savedTrackRepository.count();
    }
}
