package com.nexus.nexus.MyPackage.Controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexus.MyPackage.Dto.LikeRequest;
import com.nexus.nexus.MyPackage.Dto.VideoResponseDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.UserRepository;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;
import com.nexus.nexus.MyPackage.Services.PostServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("post")
public class PostController {
    private final PostServices postServices;
    private final UserRepository userRepository;
    private final VideosRepository videosRepository;

    @GetMapping("/getRecommendation")
    public ResponseEntity<List<VideoResponseDto>> getRecommendation(Authentication authentication) {
        UserModal currentUser = (UserModal) authentication.getPrincipal();
        List<VideosEntity> videos = postServices.getRecommendation();

        List<VideoResponseDto> response = videos.stream().map(video -> {
            Optional<UserModal> userOptional = userRepository.findByUserId(video.getUserId());
            String username = userOptional.map(UserModal::getUsername).orElse("Unknown");
            String profilePic = userOptional.map(UserModal::getProfilePic).orElse(null);

            boolean likedByCurrentUser = false;
            if (video.getLikes() != null) {
                likedByCurrentUser = video.getLikes().stream()
                        .anyMatch(like -> like.getUser().getId() == currentUser.getId());
            }

            return new VideoResponseDto(
                    String.valueOf(video.getId()),
                    video.getVideoId(),
                    video.getVideoUrl(),
                    username,
                    video.getDescription(),
                    video.getLikes() != null ? video.getLikes().size() : 0,
                    0,
                    0,
                    video.getUserId(),
                    "Original Sound",
                    video.getThumbnail(),
                    profilePic,
                    likedByCurrentUser);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/addLike")
    public ResponseEntity<String> addLike(@RequestBody LikeRequest request, Authentication authentication) {
        UserModal user = (UserModal) authentication.getPrincipal();
        String videoId = request.getVideoId();

        VideosEntity video = videosRepository.findByVideoId(videoId);
        if (video == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found");
        }

        if (postServices.isVideoLikedByUser(user, video)) {
            return ResponseEntity.ok("Already added");
        }
        String id = UUID.randomUUID().toString();

        postServices.likeVideo(user, video, id);
        return ResponseEntity.ok("Like added successfully");
    }

    @PostMapping("/removeLike")
    public ResponseEntity<String> removeLike(@RequestBody LikeRequest request, Authentication authentication) {
        UserModal user = (UserModal) authentication.getPrincipal();
        String videoId = request.getVideoId();

        VideosEntity video = videosRepository.findByVideoId(videoId);
        if (video == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found");
        }

        if (!postServices.isVideoLikedByUser(user, video)) {
            return ResponseEntity.ok("Video not liked by user");
        }

        postServices.removeLikeVideo(user, video);
        return ResponseEntity.ok("Like removed successfully");
    }
}
