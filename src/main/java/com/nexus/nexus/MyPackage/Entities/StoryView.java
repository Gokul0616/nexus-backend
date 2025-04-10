package com.nexus.nexus.MyPackage.Entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "story_views")
@Data
public class StoryView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String username;
    private String profilePic;
    private LocalDateTime viewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    @JsonIgnore
    private Story story;

    public StoryView() {
    }

    public StoryView(String userId, String username, String profilePic, LocalDateTime viewedAt, Story story) {
        this.userId = userId;
        this.username = username;
        this.profilePic = profilePic;
        this.viewedAt = viewedAt;
        this.story = story;
    }

}
