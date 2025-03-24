package com.nexus.nexus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.nexus.nexus.MyPackage.Entities.Follow;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.FollowRepository;
import com.nexus.nexus.MyPackage.Repository.UserRepository;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;

@SpringBootApplication
public class NexusApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexusApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserRepository userRepo, VideosRepository videosRepo, FollowRepository followRepo) {
		return args -> {

			UserModal user1 = new UserModal();
			user1.setUserId("user001");
			user1.setUsername("Gokul");
			user1.setFullName("Gokul R");
			user1.setEmail("gokulgokul10203@gmail.com");
			user1.setPassword("Gokul001@");
			user1.setProfilePic("https://cdn3.pixelcut.app/7/20/uncrop_hero_bdf08a8ca6.jpg");

			UserModal user2 = new UserModal();
			user2.setUserId("user002");
			user2.setUsername("Alice");
			user2.setBio("Hello");
			user2.setFullName("Alice Wonderland");
			user2.setLocation("Madurai,Tamilnadu,India");
			user2.setEmail("alice@example.com");
			user2.setPassword("Alice@123");
			user2.setProfilePic("https://randomuser.me/api/portraits/women/1.jpg");

			UserModal user3 = new UserModal();
			user3.setUserId("user003");
			user3.setUsername("Bob");
			user3.setFullName("Bob Builder");
			user3.setEmail("bob@example.com");
			user3.setPassword("Bob@1234");
			user3.setProfilePic("https://randomuser.me/api/portraits/men/2.jpg");

			UserModal user4 = new UserModal();
			user4.setUserId("user004");
			user4.setUsername("Carol");
			user4.setFullName("Carol Danvers");
			user4.setEmail("carol@example.com");
			user4.setPassword("Carol@123");
			user4.setProfilePic("https://randomuser.me/api/portraits/women/2.jpg");

			UserModal user5 = new UserModal();
			user5.setUserId("user005");
			user5.setUsername("David");
			user5.setFullName("David Smith");
			user5.setEmail("david@example.com");
			user5.setPassword("David@123");
			user5.setProfilePic("https://randomuser.me/api/portraits/men/3.jpg");

			UserModal user6 = new UserModal();
			user6.setUserId("user006");
			user6.setUsername("Eve");
			user6.setFullName("Eve Adams");
			user6.setEmail("eve@example.com");
			user6.setPassword("Eve@123");
			user6.setProfilePic("https://randomuser.me/api/portraits/women/3.jpg");

			UserModal user7 = new UserModal();
			user7.setUserId("user007");
			user7.setUsername("Frank");
			user7.setFullName("Frank Castle");
			user7.setEmail("frank@example.com");
			user7.setPassword("Frank@123");
			user7.setProfilePic("https://randomuser.me/api/portraits/men/4.jpg");

			UserModal user8 = new UserModal();
			user8.setUserId("user008");
			user8.setUsername("Grace");
			user8.setFullName("Grace Hopper");
			user8.setEmail("grace@example.com");
			user8.setPassword("Grace@123");
			user8.setProfilePic("https://randomuser.me/api/portraits/women/4.jpg");

			UserModal user9 = new UserModal();
			user9.setUserId("user009");
			user9.setUsername("Henry");
			user9.setFullName("Henry Ford");
			user9.setEmail("henry@example.com");
			user9.setPassword("Henry@123");
			user9.setProfilePic("https://randomuser.me/api/portraits/men/5.jpg");

			VideosEntity video1 = new VideosEntity();
			video1.setVideoId("video001");
			video1.setTitle("Sample Video 1");
			video1.setThumbnail("https://images.unsplash.com/photo-1575936123452-b67c3203c357?fm=jpg&q=60&w=3000");
			video1.setDescription("Description for video 1");
			video1.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4");
			video1.setUserId(user1.getUserId());

			VideosEntity video2 = new VideosEntity();
			video2.setVideoId("video002");
			video2.setTitle("Sample Video 2");
			video2.setThumbnail("https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?fm=jpg&q=60");
			video2.setDescription("Description for video 2");
			video2.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
			video2.setUserId(user1.getUserId());

			VideosEntity video3 = new VideosEntity();
			video3.setVideoId("video003");
			video3.setTitle("Alice Video 1");
			video3.setThumbnail("https://images.unsplash.com/photo-1494790108377-be9c29b29330?fm=jpg&q=60");
			video3.setDescription("Alice's first video");
			video3.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
			video3.setUserId(user2.getUserId());

			VideosEntity video4 = new VideosEntity();
			video4.setVideoId("video004");
			video4.setTitle("Alice Video 2");
			video4.setThumbnail(
					"https://images.pexels.com/photos/814830/pexels-photo-814830.jpeg?cs=srgb&dl=pexels-evgeniy-grozev-814830.jpg&fm=jpg");
			video4.setDescription("Alice's second video");
			video4.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4");
			video4.setUserId(user2.getUserId());

			VideosEntity video5 = new VideosEntity();
			video5.setVideoId("video005");
			video5.setTitle("Bob Video 1");
			video5.setThumbnail("https://images.unsplash.com/photo-1516117172878-fd2c41f4a759?fm=jpg&q=60");
			video5.setDescription("Bob's construction project video");
			video5.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4");
			video5.setUserId(user3.getUserId());

			VideosEntity video6 = new VideosEntity();
			video6.setVideoId("video006");
			video6.setTitle("Bob Video 2");
			video6.setThumbnail("https://images.unsplash.com/photo-1498050108023-c5249f4df085?fm=jpg&q=60");
			video6.setDescription("Bob's second video");
			video6.setVideoUrl(
					"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4");
			video6.setUserId(user3.getUserId());

			VideosEntity video7 = new VideosEntity();
			video7.setVideoId("video007");
			video7.setTitle("Carol Video 1");
			video7.setThumbnail("https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?fm=jpg&q=60");
			video7.setDescription("Carol's superhero journey");
			video7.setVideoUrl(
					"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4");
			video7.setUserId(user4.getUserId());

			VideosEntity video8 = new VideosEntity();
			video8.setVideoId("video008");
			video8.setTitle("Carol Video 2");
			video8.setThumbnail("https://images.unsplash.com/photo-1504198458649-3128b932f49b?fm=jpg&q=60");
			video8.setDescription("Carol's second adventure");
			video8.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
			video8.setUserId(user4.getUserId());

			VideosEntity video9 = new VideosEntity();
			video9.setVideoId("video009");
			video9.setTitle("David Video 1");
			video9.setThumbnail("https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?fm=jpg&q=60");
			video9.setDescription("David's first vlog");
			video9.setVideoUrl(
					"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4");
			video9.setUserId(user5.getUserId());

			VideosEntity video10 = new VideosEntity();
			video10.setVideoId("video010");
			video10.setTitle("David Video 2");
			video10.setThumbnail("https://images.unsplash.com/photo-1516979187457-637abb4f9353?fm=jpg&q=60");
			video10.setDescription("David's travel video");
			video10.setVideoUrl(
					"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4");
			video10.setUserId(user5.getUserId());

			VideosEntity video11 = new VideosEntity();
			video11.setVideoId("video011");
			video11.setTitle("Eve Video 1");
			video11.setThumbnail("https://images.unsplash.com/photo-1515378791036-0648a3ef77b2?fm=jpg&q=60");
			video11.setDescription("Eve's cooking tutorial");
			video11.setVideoUrl(
					"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4");
			video11.setUserId(user6.getUserId());

			VideosEntity video12 = new VideosEntity();
			video12.setVideoId("video012");
			video12.setTitle("Eve Video 2");
			video12.setThumbnail("https://images.unsplash.com/photo-1520975698518-7e3d192a0f85?fm=jpg&q=60");
			video12.setDescription("Eve's art vlog");
			video12.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
			video12.setUserId(user6.getUserId());

			VideosEntity video13 = new VideosEntity();
			video13.setVideoId("video013");
			video13.setTitle("Frank Video 1");
			video13.setThumbnail("https://images.unsplash.com/photo-1504198453319-5ce911bafcde?fm=jpg&q=60");
			video13.setDescription("Frank's action sequence");
			video13.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4");
			video13.setUserId(user7.getUserId());

			VideosEntity video14 = new VideosEntity();
			video14.setVideoId("video014");
			video14.setTitle("Frank Video 2");
			video14.setThumbnail("https://images.unsplash.com/photo-1485217988980-11786ced9454?fm=jpg&q=60");
			video14.setDescription("Frank's dramatic monologue");
			video14.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4");
			video14.setUserId(user7.getUserId());

			VideosEntity video15 = new VideosEntity();
			video15.setVideoId("video015");
			video15.setTitle("Grace Video 1");
			video15.setThumbnail("https://images.unsplash.com/photo-1544005313-94ddf0286df2?fm=jpg&q=60");
			video15.setDescription("Grace's tech talk");
			video15.setVideoUrl(
					"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4");
			video15.setUserId(user8.getUserId());

			VideosEntity video16 = new VideosEntity();
			video16.setVideoId("video016");
			video16.setTitle("Grace Video 2");
			video16.setThumbnail("https://images.unsplash.com/photo-1517841905240-472988babdf9?fm=jpg&q=60");
			video16.setDescription("Grace's tutorial");
			video16.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
			video16.setUserId(user8.getUserId());

			VideosEntity video17 = new VideosEntity();
			video17.setVideoId("video017");
			video17.setTitle("Henry Video 1");
			video17.setThumbnail("https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?fm=jpg&q=60");
			video17.setDescription("Henry's automotive review");
			video17.setVideoUrl(
					"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
			video17.setUserId(user9.getUserId());

			VideosEntity video18 = new VideosEntity();
			video18.setVideoId("video018");
			video18.setTitle("Henry Video 2");
			video18.setThumbnail("https://images.unsplash.com/photo-1498579809087-0a43f7e9f50b?fm=jpg&q=60");
			video18.setDescription("Henry's second automotive review");
			video18.setVideoUrl(
					"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4");
			video18.setUserId(user9.getUserId());
			VideosEntity video19 = new VideosEntity();
			video19.setVideoId("video019");
			video19.setTitle("Alice Video 3");
			video19.setThumbnail(
					"https://images.pexels.com/photos/814830/pexels-photo-814830.jpeg?cs=srgb&dl=pexels-evgeniy-grozev-814830.jpg&fm=jpg");
			video19.setDescription("Alice's third video");
			video19.setVideoUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4");
			video19.setUserId(user2.getUserId());
			userRepo.saveAll(Arrays.asList(user1, user2, user3, user4, user5, user6, user7, user8, user9));

			List<VideosEntity> user1Videos = new ArrayList<>();
			user1Videos.add(video1);
			user1Videos.add(video2);

			List<VideosEntity> allVideos = new ArrayList<>();
			allVideos.addAll(user1Videos);
			allVideos.addAll(Arrays.asList(
					video3, video4, video5, video6, video7, video8,
					video9, video10, video11, video12, video13, video14,
					video15, video16, video17, video18, video19));

			videosRepo.saveAll(allVideos);

			UserModal gokul = userRepo.findByUsername("Gokul")
					.orElseThrow(() -> new RuntimeException("Gokul not found"));

			List<UserModal> allUsers = userRepo.findAll();
			for (UserModal user : allUsers) {
				if (!user.getUsername().equals("Gokul")) {

					if (!followRepo.existsByFollowerAndFollowee(gokul, user)) {
						followRepo.save(new Follow(gokul, user));
					}

					if (!followRepo.existsByFollowerAndFollowee(user, gokul)) {
						followRepo.save(new Follow(user, gokul));
					}
				}
			}

			List<UserModal> others = new ArrayList<>();
			for (UserModal user : allUsers) {
				if (!user.getUsername().equals("Gokul")) {
					others.add(user);
				}
			}
			Random random = new Random();
			for (UserModal user : others) {

				int count = random.nextInt(3) + 1;
				for (int i = 0; i < count; i++) {
					UserModal randomUser = others.get(random.nextInt(others.size()));
					if (!randomUser.getUserId().equals(user.getUserId())) {
						if (!followRepo.existsByFollowerAndFollowee(user, randomUser)) {
							followRepo.save(new Follow(user, randomUser));
						}
					}
				}
			}

			System.out.println("Follow relationships created successfully!");
			System.out.println("9 users and their associated videos saved successfully!");
		};
	}
}
