package com.nexus.nexus.MyPackage.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nexus.nexus.MyPackage.Dto.StoryDto.SlideDto;
import com.nexus.nexus.MyPackage.Dto.StoryDto.StoryDto;
import com.nexus.nexus.MyPackage.Entities.Story;
import com.nexus.nexus.MyPackage.Entities.StoryView;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Repository.FollowRepository;
import com.nexus.nexus.MyPackage.Repository.StoryRepository;
import com.nexus.nexus.MyPackage.Repository.StoryViewRepository;
import com.nexus.nexus.MyPackage.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final StoryViewRepository storyViewRepository;

    public Story saveStory(MultipartFile file, String userId) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadDir = Paths.get("uploads", "Story", userId);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/stories/media/")
                .path(userId + "/")
                .path(fileName)
                .toUriString();

        String contentType = file.getContentType();
        String type;
        if (contentType != null && contentType.startsWith("video")) {
            type = "video";
        } else {
            type = "image";
        }

        Story story = new Story();
        story.setUserId(userId);
        story.setMediaUrl(fileUrl);
        story.setType(type);
        story.setCreatedAt(LocalDateTime.now());
        story.setExpireAt(LocalDateTime.now().plusHours(24));

        return storyRepository.save(story);

    }

    public List<StoryDto> getStoriesForFollowedUsers(String currentUserId) {
        // Get the list of user IDs that the current user follows.
        List<String> followedUserIds = followRepository.findFollowedUserIdsByUserId(currentUserId);

        // Option 1: Ensure current user's ID is in the list.
        // (This works if you want to simply combine the current user's story with
        // followed stories)
        if (!followedUserIds.contains(currentUserId)) {
            followedUserIds.add(currentUserId);
        }

        // Fetch all stories from these users.
        List<Story> stories = storyRepository.findByUserIdIn(followedUserIds);

        // Group stories by userId.
        Map<String, List<Story>> groupedByUser = stories.stream()
                .collect(Collectors.groupingBy(Story::getUserId));

        // Map each group to a StoryDto.
        return groupedByUser.entrySet().stream().map(entry -> {
            String storyUserId = entry.getKey();
            List<Story> userStories = entry.getValue();

            Optional<UserModal> user = userRepository.findByUserId(storyUserId);

            // Map each Story to a SlideDto.
            List<SlideDto> slides = userStories.stream()
                    .map(story -> {
                        boolean isViewed = story.getViews().stream()
                                .anyMatch(view -> view.getUserId().equals(currentUserId));
                        return new SlideDto(
                                story.getId(), // Pass slide id (story id)
                                story.getType(),
                                story.getMediaUrl(),
                                story.getViews(),
                                isViewed);
                    })
                    .collect(Collectors.toList());

            return new StoryDto(
                    storyUserId,
                    user.map(UserModal::getUsername).orElse(""),
                    user.map(UserModal::getProfilePic).orElse(""),
                    slides);
        }).collect(Collectors.toList());
    }

    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void deleteExpiredStories() {
        LocalDateTime now = LocalDateTime.now();
        List<Story> expiredStories = storyRepository.findAll().stream()
                .filter(story -> story.getExpireAt().isBefore(now))
                .collect(Collectors.toList());

        for (Story story : expiredStories) {
            storyRepository.deleteById(story.getId());

            try {
                Path path = Paths.get("uploads", "Story", story.getUserId(),
                        story.getMediaUrl().substring(story.getMediaUrl().lastIndexOf('/') + 1));
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("Failed to delete media file: " + e.getMessage());
            }
        }

        System.out.println("Expired stories cleaned up at: " + now);
    }

    public void markStoryAsViewed(Long storyId, UserModal viewer) {
        Optional<Story> storyOpt = storyRepository.findById(storyId);
        if (storyOpt.isPresent()) {
            Story story = storyOpt.get();

            // Check if already viewed (optional logic to avoid duplicates)
            boolean alreadyViewed = story.getViews().stream()
                    .anyMatch(view -> view.getUserId().equals(viewer.getUserId()));

            if (!alreadyViewed) {
                StoryView view = new StoryView(
                        viewer.getUserId(),
                        viewer.getUsername(),
                        viewer.getProfilePic(),
                        LocalDateTime.now(),
                        story);
                story.getViews().add(view);
                storyRepository.save(story); // Will cascade the new view
            }
        }
    }

    public List<StoryView> getViewersForStory(Long storyId) {
        return storyViewRepository.findByStoryId(storyId);
    }

}
