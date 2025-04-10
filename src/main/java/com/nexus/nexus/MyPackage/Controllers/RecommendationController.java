package com.nexus.nexus.MyPackage.Controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexus.MyPackage.Dto.AdVideoResponseDto;
import com.nexus.nexus.MyPackage.Dto.VideoResponseDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.FollowRepository;
import com.nexus.nexus.MyPackage.Repository.UserRepository;
import com.nexus.nexus.MyPackage.Repository.VideoLikeRepository;
import com.nexus.nexus.MyPackage.Services.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final FollowRepository followRepository;

    @GetMapping
    public ResponseEntity<List<?>> getRecommendations(
            Authentication authentication,
            @RequestParam(name = "strategy", defaultValue = "hybrid") String strategy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "20") int limit) {

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserModal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int offset = page * limit;

        UserModal currentUser = (UserModal) principal;
        List<VideosEntity> recommendedVideos;

        // Check if the user has any interaction history
        boolean hasInteractionHistory = videoLikeRepository.existsByUser(currentUser);
        if (!hasInteractionHistory) {
            recommendedVideos = recommendationService.getNewUserRecommendations(offset, limit);
        } else {
            switch (strategy.toLowerCase()) {
                case "trending":
                    recommendedVideos = recommendationService.getTrendingVideos(limit);
                    break;
                case "collaborative":
                    recommendedVideos = recommendationService.getCollaborativeRecommendations(currentUser, limit);
                    break;
                case "content-based":
                    recommendedVideos = recommendationService.getContentBasedRecommendations(currentUser, limit);
                    break;
                case "watch-based":
                    recommendedVideos = recommendationService.getWatchBasedRecommendations(currentUser, limit);
                    break;
                case "advanced":
                    recommendedVideos = recommendationService.getAdvancedRecommendations(currentUser, limit);
                    break;
                case "hybrid":
                default:
                    recommendedVideos = recommendationService.getHybridRecommendations(currentUser, limit);
                    break;
            }
        }

        List<Object> response = recommendedVideos.stream().map(video -> {
            if ("ad".equalsIgnoreCase(video.getType().toString())) {
                return new AdVideoResponseDto(
                        String.valueOf(video.getId()),
                        video.getVideoId(),
                        video.getVideoUrl(),
                        video.getThumbnail(),
                        video.getDescription(), // Assuming it's adTitle
                        "Check this out!", // adDescription placeholder
                        "Shop Now", // callToAction placeholder
                        "Advertiser Name", // Placeholder
                        "https://example.com/advertiser-pic.jpg", // Placeholder
                        "ad");
            } else {
                Optional<UserModal> userOptional = userRepository.findByUserId(video.getUserId());
                String username = userOptional.map(UserModal::getUsername).orElse("Unknown");
                String profilePic = userOptional.map(UserModal::getProfilePic).orElse(null);
                boolean likedByCurrentUser = video.getLikes() != null && video.getLikes().stream()
                        .anyMatch(like -> like.getUser().getId() == currentUser.getId());
                boolean isFollowing = followRepository.existsByFollower_UserIdAndFollowee_UserId(
                        currentUser.getUserId(), video.getUserId());

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
                        likedByCurrentUser,
                        isFollowing);
            }
        }).collect(Collectors.toList());

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

}
