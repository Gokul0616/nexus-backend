package com.nexus.nexus.MyPackage.Dto;

import java.util.List;

import com.nexus.nexus.MyPackage.Entities.VideosEntity;

import lombok.Data;

@Data
public class OtherUserProfileDto {
    private String userId;
    private String username;
    private String email;
    private String fullName;
    private String bio;
    private String location;
    private double streakPercentage;
    private String profilePic;
    private String postCount;
    private String followerCount;
    private String followingCount;
    private List<VideosEntity> videos;
    private boolean isFollowing;

    public OtherUserProfileDto(String userId, String username, String email, String fullName, String bio,
            String location, double streakPercentage, String profilePic, String postCount,
            String followerCount, String followingCount, List<VideosEntity> videos, boolean isFollowing) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.bio = bio;
        this.location = location;
        this.streakPercentage = streakPercentage;
        this.profilePic = profilePic;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.videos = videos;
        this.isFollowing = isFollowing;
    }

}
