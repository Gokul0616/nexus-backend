package com.nexus.nexus.MyPackage.Dto.StoryDto;

import java.time.LocalDateTime;

public class StoryViewDto {
    private String userId;
    private String username;
    private String profilePic;
    private LocalDateTime viewedAt;

    public StoryViewDto() {
    }

    public StoryViewDto(String userId, String username, String profilePic, LocalDateTime viewedAt) {
        this.userId = userId;
        this.username = username;
        this.profilePic = profilePic;
        this.viewedAt = viewedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
}
