package com.spotify.Example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import se.michaelthelin.spotify.SpotifyApi;

@Getter
@Setter
@Document(collection = "session")
public class SessionEntity {

    @Id
    private String id;
    private String clientId;
    private String secretId;
    private String accessToken;
    private String refreshToken;
    private Long timestamp;



}
