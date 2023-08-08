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
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiService {

    private final RestTemplate restTemplate;
    private final SavedTrackRepository savedTrackRepository;

    @Autowired
    public ApiService(RestTemplate restTemplate, SavedTrackRepository savedTrackRepository) {
        this.restTemplate = restTemplate;
        this.savedTrackRepository = savedTrackRepository;
    }

    public ResponseEntity<String> callFlaskApi() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> data = new HashMap<>();

        int count = 0;
        List<SavedTrackEntity> savedTrackList = savedTrackRepository.findAll();
        for(SavedTrackEntity track : savedTrackList) {
            data.put(track.getArtist(), track.getSongName());
            count ++;
            if (count > 5) {
                break;
            }
        }
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(data, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://127.0.0.1:5000/testSpring", requestEntity, String.class);
        return response;
    }
}
