package com.nexus.nexus.MyPackage.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nexus.nexus.MyPackage.Entities.VideosEntity;

public interface VideosRepository extends JpaRepository<VideosEntity, Long> {
    List<VideosEntity> findByUserId(String userId);

    VideosEntity findByVideoId(String videoId);

    // In your VideoRepository interface
    @Query("SELECT v FROM VideosEntity v WHERE LOWER(v.description) LIKE LOWER(concat('%', :query, '%')) " +
            "OR LOWER(v.tags) LIKE LOWER(concat('%', :query, '%'))")
    List<VideosEntity> searchVideos(@Param("query") String query);

    List<VideosEntity> findByDescriptionContainingIgnoreCaseOrTagsContainingIgnoreCase(String description, String tags);

}
