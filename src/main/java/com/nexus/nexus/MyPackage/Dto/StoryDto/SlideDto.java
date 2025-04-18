package com.nexus.nexus.MyPackage.Dto.StoryDto;

import java.util.ArrayList;
import java.util.List;

import com.nexus.nexus.MyPackage.Entities.StoryView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlideDto {
    private Long id;
    private String type; // "image" or "video"
    private String uri;
    private List<StoryView> views = new ArrayList<>();
    private boolean isViewed;
    private String placement;
}
