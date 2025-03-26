package com.nexus.nexus.MyPackage.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

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

    public void likeVideo(UserModal user, VideosEntity video, String id) {
        VideoLike videoLike = new VideoLike();
        videoLike.setUser(user);
        videoLike.setLikeId(id);
        videoLike.setVideo(video);
        videoLike.setLikedAt(LocalDateTime.now());
        videoLikeRepository.save(videoLike);
    }

}