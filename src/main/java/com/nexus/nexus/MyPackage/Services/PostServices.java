package com.nexus.nexus.MyPackage.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nexus.nexus.MyPackage.Dto.VideoRequestDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideoLike;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.UserRepository;
import com.nexus.nexus.MyPackage.Repository.VideoLikeRepository;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServices {
    private final VideosRepository videosRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final UserRepository userRepo;

    /**
     * Default recommendation fallback (if no user data is available)
     */
    public List<VideosEntity> getRecommendation() {
        return videosRepository.findAll();
    }

    /**
     * Advanced recommendation: Based on the tags of videos that the user has liked.
     * Videos matching the user's preferred tags and with higher popularity score
     * are ranked higher.
     */
    public List<VideosEntity> getRecommendationForUser(UserModal user) {
        // Get all videos
        List<VideosEntity> allVideos = videosRepository.findAll();
        // Get the videos the user has liked (assumes videoLikeRepository has this
        // method)
        List<VideoLike> likedVideos = videoLikeRepository.findAllByUser(user);

        // Extract all tags from the liked videos.
        Set<String> likedTags = new HashSet<>();
        for (VideoLike like : likedVideos) {
            VideosEntity likedVideo = like.getVideo();
            if (likedVideo.getTags() != null) {
                // Assuming tags are stored as comma-separated values
                String[] tags = likedVideo.getTags().split(",");
                for (String tag : tags) {
                    likedTags.add(tag.trim().toLowerCase());
                }
            }
        }

        // Compute a score for each video.
        List<VideoScore> scoredVideos = new ArrayList<>();
        for (VideosEntity video : allVideos) {
            double score = 0.0;
            if (video.getTags() != null) {
                String[] videoTags = video.getTags().split(",");
                for (String tag : videoTags) {
                    if (likedTags.contains(tag.trim().toLowerCase())) {
                        score += 1.0;
                    }
                }
            }
            // Add a popularity factor (e.g., each like contributes 0.1 to the score)
            if (video.getLikes() != null) {
                score += video.getLikes().size() * 0.1;
            }
            scoredVideos.add(new VideoScore(video, score));
        }

        // Filter out videos that the user has already liked.
        Set<Long> likedVideoIds = likedVideos.stream()
                .map(like -> like.getVideo().getId())
                .collect(Collectors.toSet());

        // Sort videos by score in descending order.
        List<VideosEntity> recommended = scoredVideos.stream()
                .filter(vs -> !likedVideoIds.contains(vs.video.getId()))
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .map(vs -> vs.video)
                .collect(Collectors.toList());

        // Return top 20 recommendations or fewer if not enough videos.
        return recommended.stream().limit(20).collect(Collectors.toList());
    }

    public List<VideosEntity> getTrendingVideos(int limit) {
        return videosRepository.findAll().stream()
                .sorted((v1, v2) -> Integer.compare(v2.getLikes().size(), v1.getLikes().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get recommendations using User-Based Collaborative Filtering.
     */
    public List<VideosEntity> getCollaborativeRecommendations(UserModal user, int limit) {
        List<UserModal> allUsers = userRepo.findAll();
        Map<UserModal, Double> similarityScores = new HashMap<>();
        for (UserModal otherUser : allUsers) {
            if (!otherUser.equals(user)) {
                double similarity = calculateUserSimilarity(user, otherUser);
                similarityScores.put(otherUser, similarity);
            }
        }
        List<UserModal> similarUsers = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<UserModal, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        Set<VideosEntity> recommendedVideos = new HashSet<>();
        for (UserModal similarUser : similarUsers) {
            List<VideoLike> likedVideos = videoLikeRepository.findAllByUser(similarUser);
            for (VideoLike like : likedVideos) {
                recommendedVideos.add(like.getVideo());
                if (recommendedVideos.size() >= limit) {
                    break;
                }
            }
            if (recommendedVideos.size() >= limit) {
                break;
            }
        }
        return new ArrayList<>(recommendedVideos);
    }

    /**
     * Calculate similarity between two users based on their liked videos.
     */
    private double calculateUserSimilarity(UserModal user1, UserModal user2) {
        List<VideoLike> user1Likes = videoLikeRepository.findAllByUser(user1);
        List<VideoLike> user2Likes = videoLikeRepository.findAllByUser(user2);
        Set<String> user1LikedVideoIds = user1Likes.stream()
                .map(like -> like.getVideo().getVideoId())
                .collect(Collectors.toSet());
        Set<String> user2LikedVideoIds = user2Likes.stream()
                .map(like -> like.getVideo().getVideoId())
                .collect(Collectors.toSet());
        Set<String> intersection = new HashSet<>(user1LikedVideoIds);
        intersection.retainAll(user2LikedVideoIds);
        if (intersection.isEmpty()) {
            return 0.0;
        }
        Set<String> union = new HashSet<>(user1LikedVideoIds);
        union.addAll(user2LikedVideoIds);
        return (double) intersection.size() / union.size();
    }

    /**
     * Get recommendations using Content-Based Filtering based on video tags.
     */
    public List<VideosEntity> getContentBasedRecommendations(UserModal user, int limit) {
        List<VideoLike> likedVideos = videoLikeRepository.findAllByUser(user);

        // Collect tags from liked videos, ensuring null checks
        Set<String> likedTags = likedVideos.stream()
                .map(VideoLike::getVideo)
                .map(VideosEntity::getTags)
                .filter(Objects::nonNull) // Filter out null tags
                .flatMap(tags -> Arrays.stream(tags.split(",")))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return videosRepository.findAll().stream()
                .filter(video -> {
                    String tags = video.getTags();
                    if (tags == null) {
                        return false; // Skip videos with null tags
                    }
                    Set<String> videoTags = Arrays.stream(tags.split(","))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet());
                    videoTags.retainAll(likedTags);
                    return !videoTags.isEmpty();
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get hybrid recommendations combining collaborative and content-based
     * filtering.
     */
    public List<VideosEntity> getHybridRecommendations(UserModal user, int limit) {
        List<VideosEntity> collaborativeRecommendations = getCollaborativeRecommendations(user, limit / 2);
        List<VideosEntity> contentBasedRecommendations = getContentBasedRecommendations(user, limit / 2);
        Set<VideosEntity> hybridRecommendations = new HashSet<>(collaborativeRecommendations);
        hybridRecommendations.addAll(contentBasedRecommendations);
        return new ArrayList<>(hybridRecommendations).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public boolean isVideoLikedByUser(UserModal user, VideosEntity video) {
        return videoLikeRepository.existsByUserAndVideo(user, video);
    }

    public List<VideosEntity> searchVideos(String query) {
        return videosRepository.findByDescriptionContainingIgnoreCaseOrTagsContainingIgnoreCase(query, query);
    }

    public void likeVideo(UserModal user, VideosEntity video, String id) {
        VideoLike videoLike = new VideoLike();
        videoLike.setUser(user);
        videoLike.setLikeId(id);
        videoLike.setVideo(video);
        videoLike.setLikedAt(LocalDateTime.now());
        videoLikeRepository.save(videoLike);
    }

    public String saveVideo(UserModal user, VideoRequestDto videoDto) {
        VideosEntity video = new VideosEntity();
        video.setVideoId(videoDto.getVideoId());
        video.setUserId(user.getUserId());
        video.setVideoUrl(videoDto.getVideoUrl());
        video.setThumbnail(videoDto.getThumbnailUrl());
        video.setDescription(videoDto.getDescription());
        video.setCategory(videoDto.getCategory());
        video.setTags(videoDto.getTags());
        video.setStatus("active");

        videosRepository.save(video);
        return "Video saved successfully!";
    }

    public void removeLikeVideo(UserModal user, VideosEntity video) {
        VideoLike videoLike = videoLikeRepository.findByUserAndVideo(user, video);
        if (videoLike != null) {
            videoLikeRepository.delete(videoLike);
        }
    }

    // Helper class to hold video and its computed recommendation score.
    private static class VideoScore {
        VideosEntity video;
        double score;

        VideoScore(VideosEntity video, double score) {
            this.video = video;
            this.score = score;
        }
    }
}
