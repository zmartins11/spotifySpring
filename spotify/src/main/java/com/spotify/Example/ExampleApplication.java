package com.spotify.Example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;

@SpringBootApplication(exclude = {OAuth2ClientAutoConfiguration.class})
public class ExampleApplication implements CommandLineRunner {



	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {


//		String inputFilePath = "D:\\codex-cuphead\\obsleague.mp4";
//		String outputFilePath = "D:\\codex-cuphead\\output_audio.mp3";
//		try {
//			// Use ffmpeg to convert the MP4 file to MP3
//			String ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe";
//			ProcessBuilder processBuilder = new ProcessBuilder(ffmpegPath, "-i", inputFilePath, outputFilePath);
//			Process process = processBuilder.start();
//
//
//			System.out.println("MP4 to MP3 conversion completed successfully.");
//			String tst = "tes";
//		} catch (Exception e ) {
//			e.printStackTrace();
//		}
//
//		String url = "https://www.youtube.com/watch?v=uBJHqykJ6kY&ab_channel=FIAWorldRallyChampionship";
//		String path = "D:\\codex-cuphead\\";
//		VGet v = new VGet(new URL(url), new File(path));
//		v.download();
//		String as = "done;";
	}

}
