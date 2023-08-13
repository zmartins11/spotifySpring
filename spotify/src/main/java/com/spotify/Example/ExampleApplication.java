package com.spotify.Example;

import com.spotify.Example.service.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;

@SpringBootApplication(exclude = {OAuth2ClientAutoConfiguration.class})
public class ExampleApplication implements CommandLineRunner{


	SpotifyService spotifyService;

	@Autowired
	public ExampleApplication(SpotifyService spotifyService) {
		this.spotifyService = spotifyService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		spotifyService.formatMp4();
	}
}
