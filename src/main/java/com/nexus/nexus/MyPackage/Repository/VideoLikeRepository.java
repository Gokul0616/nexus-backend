package com.nexus.nexus.MyPackage.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideoLike;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    boolean existsByUserAndVideo(UserModal user, VideosEntity video);
}
