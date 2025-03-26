package com.nexus.nexus.MyPackage.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexus.nexus.MyPackage.Entities.VideosEntity;

public interface VideosRepository extends JpaRepository<VideosEntity, Long> {
    List<VideosEntity> findByUserId(String userId);

    VideosEntity findByVideoId(String videoId);
}
