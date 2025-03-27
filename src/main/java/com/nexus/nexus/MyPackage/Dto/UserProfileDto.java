package com.nexus.nexus.MyPackage.Dto;

import java.util.List;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
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
    private List<VideosEntity> likedVideos;
}
