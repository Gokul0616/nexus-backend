package com.nexus.nexus.MyPackage.Services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideoLike;
import com.nexus.nexus.MyPackage.Entities.VideoWatch;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.UserRepository;
import com.nexus.nexus.MyPackage.Repository.VideoLikeRepository;
import com.nexus.nexus.MyPackage.Repository.VideoWatchRepository;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final VideosRepository videosRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final UserRepository userRepo;
    private final VideoWatchRepository videoWatchRepository;

    /**
     * Default recommendation fallback.
     */
    public List<VideosEntity> getRecommendation() {
        return videosRepository.findAll();
    }

    /**
     * Recommendation for new users (cold start) based on trending and diversity.
     * This strategy considers recency, overall popularity and a bit of randomness.
     */
    public List<VideosEntity> getNewUserRecommendations(int limit) {
        // Trending videos sorted by like count and recency
        List<VideosEntity> trending = videosRepository.findAll().stream()
                .sorted((v1, v2) -> {
                    // Combine likes and recency (assumes VideosEntity has a createdAt property)
                    long likesDiff = v2.getLikes() != null ? v2.getLikes().size()
                            : 0L -
                                    (v1.getLikes() != null ? v1.getLikes().size() : 0L);
                    long daysDiff = ChronoUnit.DAYS.between(v2.getCreatedAt(), v1.getCreatedAt());
                    return Long.compare(likesDiff + daysDiff, 0);
                })
                .limit(limit * 2) // take more than limit to allow for later diversification
                .collect(Collectors.toList());

        // Shuffle for diversity and pick the final list
        Collections.shuffle(trending);
        return trending.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Get advanced recommendations for existing users.
     * Combines collaborative, content-based, watch-based, and ML-scored features.
     */
    public List<VideosEntity> getAdvancedRecommendations(UserModal user, int limit) {
        // Base recommendations from different strategies
        List<VideosEntity> collaborative = getCollaborativeRecommendations(user, limit);
        List<VideosEntity> contentBased = getContentBasedRecommendations(user, limit);
        List<VideosEntity> watchBased = getWatchBasedRecommendations(user, limit);

        // Combine all recommendations into a unique set
        Set<VideosEntity> combined = new HashSet<>();
        combined.addAll(collaborative);
        combined.addAll(contentBased);
        combined.addAll(watchBased);

        // Get all candidate videos (if none available from above, fallback to trending)
        if (combined.isEmpty()) {
            combined.addAll(getTrendingVideos(limit));
        }

        // Now compute an advanced engagement score for each candidate video using
        // client data.
        // In production, you might replace this with a call to an ML model or AI
        // service.
        List<VideoScore> scoredVideos = combined.stream()
                .map(video -> new VideoScore(video, computeEngagementScore(user, video)))
                .collect(Collectors.toList());

        // Sort videos by computed score (higher is better)
        List<VideosEntity> recommended = scoredVideos.stream()
                .sorted((vs1, vs2) -> Double.compare(vs2.score, vs1.score))
                .map(vs -> vs.video)
                .limit(limit)
                .collect(Collectors.toList());

        return recommended;
    }

    /**
     * A dummy method to compute an engagement score based on client data.
     * This factors in likes, watch completion ratio, recency, and an artificial AI
     * boost.
     * Replace this with a real model inference in a production system.
     */
    private double computeEngagementScore(UserModal user, VideosEntity video) {
        double score = 0.0;

        // Factor 1: Popularity (likes count)
        int likes = video.getLikes() != null ? video.getLikes().size() : 0;
        score += likes * 0.2;

        // Factor 2: Watch behavior (how many times has the user fully watched it?)
        long fullWatchCount = videoWatchRepository.findAllByUser(user).stream()
                .filter(watch -> watch.isFullyWatched() && watch.getVideoId().equals(video.getVideoId()))
                .count();
        score += fullWatchCount * 1.0;

        // Factor 3: Recency (more recent videos get a slight boost)
        if (video.getCreatedAt() != null) {
            long daysSinceCreated = ChronoUnit.DAYS.between(video.getCreatedAt(), LocalDateTime.now());
            score += Math.max(0, 10 - daysSinceCreated) * 0.3;
        }

        // Factor 4: AI/ML predicted engagement (simulate with a random boost or a fixed
        // heuristic)
        // In a real scenario, this might call an external AI service/model.
        double aiBoost = predictEngagementScore(user, video);
        score += aiBoost;

        return score;
    }

    /**
     * Dummy ML model integration.
     * Replace this logic with an actual ML inference call.
     */
    private double predictEngagementScore(UserModal user, VideosEntity video) {
        // For demonstration, we add a pseudo-random boost based on video and user IDs.
        int hash = Objects.hash(user.getUserId(), video.getVideoId());
        // Normalize the hash to a range (for example, 0 to 5)
        double boost = (Math.abs(hash) % 500) / 100.0;
        return boost;
    }

    /**
     * Get trending videos.
     */
    public List<VideosEntity> getTrendingVideos(int limit) {
        return videosRepository.findAll().stream()
                .sorted((v1, v2) -> Integer.compare(
                        v2.getLikes() != null ? v2.getLikes().size() : 0,
                        v1.getLikes() != null ? v1.getLikes().size() : 0))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Collaborative Filtering Recommendation.
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
     * Content-Based Filtering Recommendation.
     */
    public List<VideosEntity> getContentBasedRecommendations(UserModal user, int limit) {
        List<VideoLike> likedVideos = videoLikeRepository.findAllByUser(user);
        Set<String> likedTags = likedVideos.stream()
                .map(VideoLike::getVideo)
                .map(VideosEntity::getTags)
                .filter(Objects::nonNull)
                .flatMap(tags -> Arrays.stream(tags.split(",")))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return videosRepository.findAll().stream()
                .filter(video -> {
                    String tags = video.getTags();
                    if (tags == null) {
                        return false;
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
     * Watch-Based Recommendation.
     */
    public List<VideosEntity> getWatchBasedRecommendations(UserModal user, int limit) {
        List<VideoWatch> watchRecords = videoWatchRepository.findAllByUser(user);
        Map<String, Long> fullWatchCount = watchRecords.stream()
                .filter(VideoWatch::isFullyWatched)
                .collect(Collectors.groupingBy(VideoWatch::getVideoId, Collectors.counting()));

        return videosRepository.findAll().stream()
                .filter(video -> fullWatchCount.containsKey(video.getVideoId()))
                .sorted((v1, v2) -> Long.compare(
                        fullWatchCount.get(v2.getVideoId()),
                        fullWatchCount.get(v1.getVideoId())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Hybrid Recommendation: combines collaborative and content-based.
     */

    /**
     * Hybrid Recommendation: Combines collaborative, content-based, watch-based,
     * and trending strategies.
     */
    public List<VideosEntity> getHybridRecommendations(UserModal user, int limit) {
        // Get recommendations from various strategies.
        List<VideosEntity> collaborative = getCollaborativeRecommendations(user, limit);
        List<VideosEntity> contentBased = getContentBasedRecommendations(user, limit);
        List<VideosEntity> watchBased = getWatchBasedRecommendations(user, limit);
        List<VideosEntity> trending = getTrendingVideos(limit);

        // Combine all recommendations into a unique candidate set.
        Set<VideosEntity> candidateSet = new HashSet<>();
        candidateSet.addAll(collaborative);
        candidateSet.addAll(contentBased);
        candidateSet.addAll(watchBased);
        candidateSet.addAll(trending);

        // If the candidate set is empty, fallback to a default recommendation.
        if (candidateSet.isEmpty()) {
            candidateSet.addAll(getRecommendation());
        }

        // Optionally, compute an advanced engagement score for each candidate video.
        // This score uses likes, watch behavior, recency, and a dummy AI boost.
        List<VideoScore> scoredCandidates = candidateSet.stream()
                .map(video -> new VideoScore(video, computeEngagementScore(user, video)))
                .collect(Collectors.toList());

        // Sort the candidates by their computed score (highest first).
        List<VideosEntity> sortedRecommendations = scoredCandidates.stream()
                .sorted((vs1, vs2) -> Double.compare(vs2.score, vs1.score))
                .map(vs -> vs.video)
                .collect(Collectors.toList());

        // Return the top 'limit' recommendations.
        return sortedRecommendations.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Calculate similarity between two users based on liked videos.
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

    private static class VideoScore {
        VideosEntity video;
        double score;

        VideoScore(VideosEntity video, double score) {
            this.video = video;
            this.score = score;
        }
    }
}
