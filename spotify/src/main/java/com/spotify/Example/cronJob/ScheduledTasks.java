package com.spotify.Example.cronJob;

import com.spotify.Example.enums.KeysEnum;
import com.spotify.Example.model.SessionEntity;
import com.spotify.Example.repository.SessionRepository;
import com.spotify.Example.service.SpotifyService;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {

    private SessionRepository sessionRepository;
    private SpotifyService spotifyService;

    @Autowired
    public ScheduledTasks(SessionRepository sessionRepository, SpotifyService spotifyService) {
        this.sessionRepository = sessionRepository;
        this.spotifyService = spotifyService;
    }


    @Scheduled(fixedDelay = 10000)
    public void scheduleFixedDelayTask() throws IOException, ParseException, SpotifyWebApiException {

        SessionEntity session = spotifyService.getSession();

        if (session != null) {
            SpotifyApi spotifyApi = new SpotifyApi.Builder()
                    .setClientId(KeysEnum.CLIENT_ID.getKey())
                    .setClientSecret(KeysEnum.CLIENT_SECRET.getKey())
                    .setAccessToken(session.getAccessToken())
                    .setRefreshToken(session.getRefreshToken())
                    .build();

//

            SavedTrack[] savedTrackList = spotifyService.getUserTracks(spotifyApi);
            int numberTracks = savedTrackList.length;
            if(numberTracks == 0) { //token expired : replace tokens

                SpotifyApi newSpotifyApi = spotifyService.handleTokenExpired(spotifyApi);
                // delete current token and save new token in database
                spotifyService.storeSession(spotifyApi);

                numberTracks = spotifyService.getNumberOfSavedTracks(newSpotifyApi);
            }
            //logic to compare currentTracks and saved tracks
            System.out.println("tracks in playlist: " + numberTracks);

            //get number of tracks in database
            int savedDbTracks = spotifyService.getNumberDbTracks();
            System.out.println("tracks in database : " + savedDbTracks);
            if(savedDbTracks < numberTracks) {
                int newSongs = numberTracks - savedDbTracks;
                System.out.println("number of new songs:" + newSongs);
                SavedTrack[] newSongsToAdd = Arrays.stream(savedTrackList).limit(newSongs).collect(Collectors.toList()).toArray(new SavedTrack[newSongs]);
                spotifyService.saveTracks(newSongsToAdd);
            }

        }



    }
}
