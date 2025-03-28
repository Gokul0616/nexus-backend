package com.nexus.nexus.MyPackage.Controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexus.MyPackage.Dto.UserSearchResultDto;
import com.nexus.nexus.MyPackage.Dto.VideoResponseDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Services.MyUserServices;
import com.nexus.nexus.MyPackage.Services.PostServices;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final MyUserServices myUserServices;
    private final PostServices myVideoServices;

    public SearchController(MyUserServices myUserServices, PostServices myVideoServices) {
        this.myUserServices = myUserServices;
        this.myVideoServices = myVideoServices;
    }

    @GetMapping
    public ResponseEntity<?> search(@RequestParam String query, @RequestParam String type) {
        if ("users".equalsIgnoreCase(type)) {
            List<UserSearchResultDto> users = myUserServices.searchUsers(query);
            return ResponseEntity.ok(users);
        } else if ("videos".equalsIgnoreCase(type)) {
            List<VideosEntity> videos = myVideoServices.searchVideos(query);
            UserModal currentUser = (UserModal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            List<VideoResponseDto> result = videos.stream().map(video -> {
                boolean likedByCurrentUser = false;
                if (video.getLikes() != null && currentUser != null) {
                    likedByCurrentUser = video.getLikes().stream()
                            .anyMatch(like -> like.getUser().getId() == currentUser.getId());
                }
                return new VideoResponseDto(
                        String.valueOf(video.getId()),
                        video.getVideoId(),
                        video.getVideoUrl(),
                        myUserServices.getUsernameByUserId(video.getUserId()),
                        video.getDescription(),
                        video.getLikes() != null ? video.getLikes().size() : 0,
                        0, // comments count (update as needed)
                        0, // shares count (update as needed)
                        video.getUserId(),
                        "",
                        video.getThumbnail(),
                        myUserServices.getProfilePicByUserId(video.getUserId()),
                        likedByCurrentUser);
            }).collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body("Invalid search type");
        }
    }
}
