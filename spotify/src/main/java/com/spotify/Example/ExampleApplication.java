package com.spotify.Example;

import com.spotify.Example.service.GDriveService;
import com.spotify.Example.service.SpotifyService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.context.event.EventListener;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;

@SpringBootApplication()
public class ExampleApplication implements ApplicationRunner {


	SpotifyService spotifyService;
	GDriveService gDriveService;

	@Autowired
	public ExampleApplication(SpotifyService spotifyService, GDriveService gDriveService) {
		this.spotifyService = spotifyService;
		this.gDriveService = gDriveService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {


//		gDriveService.uploadFile(multipartFile);

		gDriveService.listFolderContent();
	}
}
