package com.nexus.nexus;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.UserRepository;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;

@SpringBootApplication
public class NexusApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexusApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserRepository userRepo, VideosRepository videosRepo) {
		return args -> {
			UserModal user = new UserModal();
			user.setUsername("Gokul");
			user.setEmail("gokul@gmail.com");
			user.setPassword("Gokul001@");
			user.setFullName("Gokul");
			user.setUserId("user123");
			user.setProfilePic("https://cdn3.pixelcut.app/7/20/uncrop_hero_bdf08a8ca6.jpg");

			VideosEntity video1 = new VideosEntity();
			video1.setVideoId("video001");
			video1.setTitle("Sample Video 1");
			video1.setThumbnail(
					"https://images.unsplash.com/photo-1575936123452-b67c3203c357?fm=jpg&q=60&w=3000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aW1hZ2V8ZW58MHx8MHx8fDA%3D");
			video1.setDescription("Description for video 1");
			video1.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4");
			video1.setUserId(user.getUserId());

			VideosEntity video2 = new VideosEntity();
			video2.setVideoId("video002");
			video2.setTitle("Sample Video 2");
			video2.setDescription("Description for video 2");
			video2.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
			video2.setUserId(user.getUserId());

			userRepo.save(user);
			videosRepo.saveAll(List.of(video1, video2));

			System.out.println("User " + user.getUsername() + " and associated videos saved");
		};
	}

}
