package com.nexus.nexus.MyPackage.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideoLike;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    boolean existsByUserAndVideo(UserModal user, VideosEntity video);

    VideoLike findByUserAndVideo(UserModal user, VideosEntity video);

    List<VideoLike> findByUser(UserModal user);

    List<VideoLike> findAllByUser(UserModal user);

    boolean existsByUser(UserModal currentUser);
}
