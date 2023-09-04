package com.spotify.Example.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
@EnableScheduling
public class Config {

    @Value("${google.drive.clientId}")
    private String clientId;

    @Value("${google.drive.clientSecret}")
    private String clientSecret;


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
