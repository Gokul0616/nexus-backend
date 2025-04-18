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
    private static final Path BASE_UPLOAD_DIR = Paths.get("uploads", "Story");

    static {
        try {
            Files.createDirectories(BASE_UPLOAD_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create base upload directory", e);
        }
    }

    public Story saveStory(MultipartFile file, String userId, String placement) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadDir = Paths.get(BASE_UPLOAD_DIR.toString(), userId);
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
        story.setPlacement(placement);

        return storyRepository.save(story);

    }

    public List<StoryDto> getStoriesForFollowedUsers(String currentUserId) {

        List<String> followedUserIds = followRepository.findFollowedUserIdsByUserId(currentUserId);

        if (!followedUserIds.contains(currentUserId)) {
            followedUserIds.add(currentUserId);
        }

        List<Story> stories = storyRepository.findByUserIdIn(followedUserIds);

        Map<String, List<Story>> groupedByUser = stories.stream()
                .collect(Collectors.groupingBy(Story::getUserId));

        return groupedByUser.entrySet().stream().map(entry -> {
            String storyUserId = entry.getKey();
            List<Story> userStories = entry.getValue();

            Optional<UserModal> user = userRepository.findByUserId(storyUserId);

            List<SlideDto> slides = userStories.stream()
                    .map(story -> {
                        boolean isViewed = story.getViews().stream()
                                .anyMatch(view -> view.getUserId().equals(currentUserId));
                        return new SlideDto(
                                story.getId(),
                                story.getType(),
                                story.getMediaUrl(),
                                story.getViews(),
                                isViewed,
                                story.getPlacement());
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

            boolean alreadyViewed = story.getViews().stream()
                    .anyMatch(view -> view.getUserId().equals(viewer.getUserId()));

            if (!alreadyViewed) {
                StoryView view = new StoryView(null,
                        viewer.getUserId(),
                        viewer.getUsername(),
                        viewer.getProfilePic(),
                        LocalDateTime.now(),
                        story);
                story.getViews().add(view);
                storyRepository.save(story);
            }
        }
    }

    public List<StoryView> getViewersForStory(Long storyId) {
        return storyViewRepository.findByStoryId(storyId);
    }

}
