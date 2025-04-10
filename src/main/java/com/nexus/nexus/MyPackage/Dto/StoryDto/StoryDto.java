package com.nexus.nexus.MyPackage.Dto.StoryDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class StoryDto {
    private String id;
    private String username;
    private String avatar;
    private List<SlideDto> slides;

}
