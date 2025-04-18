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
import com.nexus.nexus.MyPackage.Dto.VideoRequestDto;
import com.nexus.nexus.MyPackage.Dto.VideoResponseDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.UserRepository;
import com.nexus.nexus.MyPackage.Repository.VideoLikeRepository;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;
import com.nexus.nexus.MyPackage.Services.PostServices;
import com.nexus.nexus.MyPackage.Services.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("post")
public class PostController {
    private final PostServices postServices;
    private final UserRepository userRepository;
    private final VideosRepository videosRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final RecommendationService recommendationService;

    @PostMapping("/savePost")
    public ResponseEntity<?> savePost(@RequestBody VideoRequestDto videoDto, Authentication authentication) {
        UserModal user = (UserModal) authentication.getPrincipal();

        return ResponseEntity.ok(postServices.saveVideo(user, videoDto));
    }

    @GetMapping("/getRecommendation")
    public ResponseEntity<List<VideoResponseDto>> getRecommendation(Authentication authentication) {
        UserModal currentUser = (UserModal) authentication.getPrincipal();
        List<VideosEntity> videos = postServices.getRecommendationForUser(currentUser);

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
                    video.getType(),
                    profilePic,
                    likedByCurrentUser, false);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // @GetMapping("/recommendations")
    // public ResponseEntity<List<VideoResponseDto>> getRecommendations(
    // Authentication authentication,
    // @RequestParam(name = "strategy", defaultValue = "hybrid") String strategy,
    // @RequestParam(name = "limit", defaultValue = "20") int limit) {

    // Object principal = authentication.getPrincipal();
    // if (!(principal instanceof UserModal)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }
    // UserModal currentUser = (UserModal) principal;
    // List<VideosEntity> recommendedVideos;

    // // Check if the user has any interaction history
    // boolean hasInteractionHistory =
    // videoLikeRepository.existsByUser(currentUser);
    // if (!hasInteractionHistory) {
    // // Cold Start: New user
    // recommendedVideos = recommendationService.getNewUserRecommendations(limit);
    // } else {
    // switch (strategy.toLowerCase()) {
    // case "trending":
    // recommendedVideos = recommendationService.getTrendingVideos(limit);
    // break;
    // case "collaborative":
    // recommendedVideos =
    // recommendationService.getCollaborativeRecommendations(currentUser, limit);
    // break;
    // case "content-based":
    // recommendedVideos =
    // recommendationService.getContentBasedRecommendations(currentUser, limit);
    // break;
    // case "watch-based":
    // recommendedVideos =
    // recommendationService.getWatchBasedRecommendations(currentUser, limit);
    // break;
    // case "advanced":
    // recommendedVideos =
    // recommendationService.getAdvancedRecommendations(currentUser, limit);
    // break;
    // case "hybrid":
    // default:
    // recommendedVideos =
    // recommendationService.getHybridRecommendations(currentUser, limit);
    // break;
    // }
    // }

    // List<VideoResponseDto> response = recommendedVideos.stream().map(video -> {
    // Optional<UserModal> userOptional =
    // userRepository.findByUserId(video.getUserId());
    // String username = userOptional.map(UserModal::getUsername).orElse("Unknown");
    // String profilePic = userOptional.map(UserModal::getProfilePic).orElse(null);
    // boolean likedByCurrentUser = video.getLikes() != null &&
    // video.getLikes().stream()
    // .anyMatch(like -> like.getUser().getId() == currentUser.getId());
    // return new VideoResponseDto(
    // String.valueOf(video.getId()),
    // video.getVideoId(),
    // video.getVideoUrl(),
    // username,
    // video.getDescription(),
    // video.getLikes() != null ? video.getLikes().size() : 0,
    // 0,
    // 0,
    // video.getUserId(),
    // "Original Sound",
    // video.getThumbnail(),
    // profilePic,
    // likedByCurrentUser);
    // }).collect(Collectors.toList());

    // if (response.isEmpty()) {
    // return ResponseEntity.noContent().build();
    // }

    // return ResponseEntity.ok(response);
    // }

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
