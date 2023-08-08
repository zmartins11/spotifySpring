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

    public void formatMp4() {
        String inputFilePath = "D:\\codex-cuphead\\obsleague.mp4";
		String outputFilePath = "D:\\codex-cuphead\\output_audio.mp3";
		try {
			// Use ffmpeg to convert the MP4 file to MP3
			String ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe";
			ProcessBuilder processBuilder = new ProcessBuilder(ffmpegPath, "-i", inputFilePath, outputFilePath);
			Process process = processBuilder.start();


			System.out.println("MP4 to MP3 conversion completed successfully.");
			String tst = "tes";
		} catch (Exception e ) {
			e.printStackTrace();
		}
    }
}
