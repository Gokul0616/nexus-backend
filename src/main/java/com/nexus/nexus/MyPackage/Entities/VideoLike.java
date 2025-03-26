package com.nexus.nexus.MyPackage.Entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "video_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String likeId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private UserModal user; // Fix: Reference UserModal.id (long)

    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "videoId", nullable = false)
    @JsonIgnore
    private VideosEntity video;

    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt = LocalDateTime.now();
}
