package com.nexus.nexus.MyPackage.Controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nexus.nexus.MyPackage.Dto.StoryDto.StoryDto;
import com.nexus.nexus.MyPackage.Entities.Story;
import com.nexus.nexus.MyPackage.Entities.StoryView;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Services.StoryService;
import com.nexus.nexus.MyPackage.WebSocket.MyWebSocketHandler;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    // Endpoint for uploading a story via a multipart file
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Story> uploadStory(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("placement") String placement) {
        try {
            Story savedStory = storyService.saveStory(file, userId, placement);
            MyWebSocketHandler.sendNewStory(savedStory);

            return ResponseEntity.ok(savedStory);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint to get stories for users the current user follows
    // @GetMapping
    // public ResponseEntity<List<Story>> getStories(Authentication authentication)
    // {
    // UserModal userDetails = (UserModal) authentication.getPrincipal();
    // String userId = userDetails.getUserId(); // Adjust based on your user details
    // implementation

    // List<Story> stories = storyService.getStoriesForFollowedUsers(userId);
    // return ResponseEntity.ok(stories);
    // }
    @GetMapping
    public ResponseEntity<List<StoryDto>> getFollowedStories(Authentication authentication) {
        UserModal userDetails = (UserModal) authentication.getPrincipal();
        String currentUserId = userDetails.getUserId();
        List<StoryDto> stories = storyService.getStoriesForFollowedUsers(currentUserId);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/media/{userId}/{fileName}")
    public ResponseEntity<?> getStoryMedia(@PathVariable(name = "userId") String userId,
            @PathVariable(name = "fileName") String fileName)
            throws IOException {
        Path filePath = Paths.get("uploads", "Story", userId, fileName);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(filePath);
        byte[] fileBytes = Files.readAllBytes(filePath);

        return ResponseEntity
                .ok()
                .header("Content-Type", contentType)
                .body(fileBytes);
    }

    @PostMapping("/view")
    public ResponseEntity<?> markStoryAsViewed(
            @RequestBody MarkStoryViewRequest request,
            Authentication authentication) {
        UserModal viewer = (UserModal) authentication.getPrincipal();
        Long slideId = request.getStoryId(); // Extract the story/slide ID

        try {
            storyService.markStoryAsViewed(slideId, viewer);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark story as viewed.");
        }
    }

    @GetMapping("/viewers")
    public ResponseEntity<?> getViewers(@RequestBody Long storyId) {
        List<StoryView> viewers = storyService.getViewersForStory(storyId);
        return ResponseEntity.ok(viewers);
    }

    @PostMapping("/view/batch")
    public ResponseEntity<?> markMultipleSlidesViewed(
            @RequestBody List<Long> slideIds,
            Authentication authentication) {
        UserModal viewer = (UserModal) authentication.getPrincipal();
        slideIds.forEach(id -> storyService.markStoryAsViewed(id, viewer));
        return ResponseEntity.ok().build();
    }

    public static class MarkStoryViewRequest {
        private Long storyId;

        public MarkStoryViewRequest() {
        }

        public Long getStoryId() {
            return storyId;
        }

        public void setStoryId(Long storyId) {
            this.storyId = storyId;
        }
    }
}