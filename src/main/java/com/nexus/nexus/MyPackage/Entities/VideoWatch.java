package com.nexus.nexus.MyPackage.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "video_watch")
public class VideoWatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoId;

    private double watchTime;

    private boolean fullyWatched;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModal user;

    public VideoWatch() {
    }

    public VideoWatch(String videoId, double watchTime, boolean fullyWatched, UserModal user) {
        this.videoId = videoId;
        this.watchTime = watchTime;
        this.fullyWatched = fullyWatched;
        this.user = user;
    }

}
