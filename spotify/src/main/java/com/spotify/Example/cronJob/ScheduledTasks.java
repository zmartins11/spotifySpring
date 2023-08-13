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

import java.io.IOException;

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
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        SessionEntity session  = (SessionEntity) sessionRepository.findAll(sort).stream().findFirst().orElse(null);

        if (session != null) {
            SpotifyApi spotifyApi = new SpotifyApi.Builder()
                    .setClientId(KeysEnum.CLIENT_ID.getKey())
                    .setClientSecret(KeysEnum.CLIENT_SECRET.getKey())
                    .setAccessToken(session.getAccessToken())
                    .setRefreshToken(session.getRefreshToken())
                    .build();

            spotifyService.getUserTracks(spotifyApi);
        }

    }
}
