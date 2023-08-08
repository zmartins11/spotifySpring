package com.spotify.Example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "saved_tracks")
public class SavedTrackEntity {
    @Id
    private String id;
    private String songName;
    private String artist;
}
