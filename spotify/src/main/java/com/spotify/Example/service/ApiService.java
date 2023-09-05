package com.spotify.Example.service;

import com.spotify.Example.model.SavedTrackEntity;
import com.spotify.Example.repository.SavedTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;

import java.util.*;

@Service
public class ApiService {

    private final RestTemplate restTemplate;
    private final SavedTrackRepository savedTrackRepository;

    @Autowired
    public ApiService(RestTemplate restTemplate, SavedTrackRepository savedTrackRepository) {
        this.restTemplate = restTemplate;
        this.savedTrackRepository = savedTrackRepository;
    }

    public ResponseEntity<String> callFlaskApi(SavedTrack[] newSongsToAdd) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> data = new HashMap<>();
        Random random = new Random();

        List<SavedTrackEntity> savedTrackList = savedTrackRepository.findAll();
        for (SavedTrack track : newSongsToAdd) {
            String artistName = Arrays.stream(track.getTrack().getArtists())
                    .findFirst()
                    .map(ArtistSimplified::getName)
                    .orElse("");
            String trackName = track.getTrack().getName();

            if (data.containsKey(artistName)) {
                // If it is, append a random integer to the artist name
                artistName = artistName + random.nextInt(1000);
                data.put(artistName, trackName);
            } else {
                // If it's not, simply put the artist and track name in the map
                data.put(artistName, trackName);
            }
        }
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(data, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://127.0.0.1:5000/testSpring", requestEntity, String.class);
        return response;
    }
}
