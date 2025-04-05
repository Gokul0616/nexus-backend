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
    public ResponseEntity<List<VideoResponseDto>> getRecommendations(
            Authentication authentication,
            @RequestParam(name = "strategy", defaultValue = "hybrid") String strategy,
            @RequestParam(name = "limit", defaultValue = "20") int limit) {

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserModal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserModal currentUser = (UserModal) principal;
        List<VideosEntity> recommendedVideos;

        // Check if the user has any interaction history
        boolean hasInteractionHistory = videoLikeRepository.existsByUser(currentUser);
        if (!hasInteractionHistory) {
            // Cold Start: New user
            recommendedVideos = recommendationService.getNewUserRecommendations(limit);
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

        List<VideoResponseDto> response = recommendedVideos.stream().map(video -> {
            Optional<UserModal> userOptional = userRepository.findByUserId(video.getUserId());
            String username = userOptional.map(UserModal::getUsername).orElse("Unknown");
            String profilePic = userOptional.map(UserModal::getProfilePic).orElse(null);
            boolean likedByCurrentUser = video.getLikes() != null && video.getLikes().stream()
                    .anyMatch(like -> like.getUser().getId() == currentUser.getId());
            boolean isFollowing = false;

            isFollowing = followRepository.existsByFollower_UserIdAndFollowee_UserId(
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
                    profilePic,
                    likedByCurrentUser, isFollowing);
        }).collect(Collectors.toList());

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

}
