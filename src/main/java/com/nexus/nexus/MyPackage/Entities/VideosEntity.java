package com.nexus.nexus.MyPackage.Entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "videos_modal")
@Getter
@Setter
public class VideosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String videoId;

    private String title;
    private String description;
    private String thumbnail;
    private String videoUrl;
    private String category;
    private String tags;
    private String status;
    private String createdAt;
    private String updatedAt;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoLike> likes;
}
