package com.spotify.Example.service;

import com.spotify.Example.model.SavedTrackEntity;
import com.spotify.Example.repository.SavedTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;

import java.util.Arrays;

@Service
public class SpotifyService {

    SavedTrackRepository savedTrackRepository;

    @Autowired
    public SpotifyService(SavedTrackRepository savedTrackRepository) {
        this.savedTrackRepository = savedTrackRepository;
    }

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
}
