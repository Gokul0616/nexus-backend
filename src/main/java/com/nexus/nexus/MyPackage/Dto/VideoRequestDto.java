package com.nexus.nexus.MyPackage.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoRequestDto {
    private String videoId;
    private String videoUrl;
    private String thumbnailUrl;
    private String description;
    private String category;
    private String tags;
}
