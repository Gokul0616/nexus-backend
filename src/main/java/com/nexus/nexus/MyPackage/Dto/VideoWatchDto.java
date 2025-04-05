package com.nexus.nexus.MyPackage.Dto;

import lombok.Data;

@Data
public class VideoWatchDto {
    private String videoId;
    private double watchTime; // in seconds
    private String userId; // optional, if you want to track which user
    private boolean fullyWatched;
}
