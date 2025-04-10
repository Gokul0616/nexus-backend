package com.nexus.nexus.MyPackage.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdVideoResponseDto {
    private String id;
    private String videoId;
    private String videoUrl;
    private String thumbnail;
    private String adTitle;
    private String adDescription;
    private String callToAction;
    private String advertiserName;
    private String advertiserProfilePic;
    private String type; // Should always be "ad"
}
