package com.spotify.Example.service;

import com.spotify.Example.model.SavedTrackEntity;
import com.spotify.Example.repository.SavedTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;

import java.io.File;
import java.io.IOException;
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
        String inputFolder = "D:\\codex-cuphead";
		String outpuFolder = "D:\\songsFormatMP3";
        String ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe";

        File inputDir = new File(inputFolder);
        File [] videoFiles = inputDir.listFiles((dir, name) -> name.endsWith(".mp4"));


        if (videoFiles != null) {
            for (File videoFile : videoFiles) {
                String inputFilePath = videoFile.getAbsolutePath();
                String outputFilePath = outpuFolder + "\\" + videoFile.getName().replace(".mp4", ".mp3");

                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(ffmpegPath, "-i", inputFilePath, outputFilePath);
                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();

                    if(exitCode == 0) {
                        System.out.println("Convertion of " + videoFile.getName() + " completed successfully");
                    } else {
                        System.out.println("Convertion of " + videoFile.getName() + " failed with exit code: " + exitCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
         }

    }
}
