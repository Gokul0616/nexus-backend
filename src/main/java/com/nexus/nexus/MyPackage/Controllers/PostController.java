package com.nexus.nexus.MyPackage.Controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexus.MyPackage.Dto.VideoResponseDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.UserRepository;
import com.nexus.nexus.MyPackage.Services.PostServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("post")
public class PostController {
    private final PostServices postServices;
    private final UserRepository userRepository;

    @GetMapping("/getRecommendation")
    public ResponseEntity<List<VideoResponseDto>> getRecommendation() {
        List<VideosEntity> videos = postServices.getRecommendation();
        List<VideoResponseDto> response = videos.stream().map(video -> {

            Optional<UserModal> userOptional = userRepository.findByUserId(video.getUserId());

            String username = userOptional.map(UserModal::getUsername).orElse("Unknown");
            String profilePic = userOptional.map(UserModal::getProfilePic).orElse(null);

            return new VideoResponseDto(
                    String.valueOf(video.getId()),
                    video.getVideoUrl(),
                    username,
                    video.getTitle(),
                    video.getDescription(),
                    0,
                    0,
                    0,
                    video.getUserId(),
                    "Original Sound",
                    video.getThumbnail(),
                    profilePic);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
