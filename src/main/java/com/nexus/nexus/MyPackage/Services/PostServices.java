package com.nexus.nexus.MyPackage.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nexus.nexus.MyPackage.Dto.VideoRequestDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideoLike;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.VideoLikeRepository;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServices {
    private final VideosRepository videosRepository;
    private final VideoLikeRepository videoLikeRepository;

    public List<VideosEntity> getRecommendation() {
        return videosRepository.findAll();
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
}
