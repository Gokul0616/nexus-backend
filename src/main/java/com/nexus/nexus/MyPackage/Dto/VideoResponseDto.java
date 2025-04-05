package com.nexus.nexus.MyPackage.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VideoResponseDto {
    private String id;
    private String videoId;
    private String videoSource;
    private String username;
    private String caption;
    private int likes;
    private int comments;
    private int shares;
    private String userId;
    private String musicTitle;
    private String thumbnail;
    private String profilePic;
    private boolean likedByCurrentUser;
    private boolean followedByCurrentUser;
}
