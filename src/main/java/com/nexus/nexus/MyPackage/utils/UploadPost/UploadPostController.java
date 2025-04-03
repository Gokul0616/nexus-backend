package com.nexus.nexus.MyPackage.utils.UploadPost;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nexus.nexus.MyPackage.Configuration.JwtRequestUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("posts")
public class UploadPostController {

    private final JwtRequestUtil jwtUtil;

    private static final Path BASE_UPLOAD_DIR = Paths.get("uploads", "videos");

    static {
        try {
            Files.createDirectories(BASE_UPLOAD_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create base upload directory", e);
        }
    }

    /**
     * This endpoint accepts two files: a video and its corresponding thumbnail.
     * The files are saved under the directory structure:
     *
     * uploads/videos/{username}/{videoId}/
     * video_{videoId}.ext
     * thumbnail-{videoId}.ext
     *
     * Both files will share the same unique videoId.
     * The response contains videoUrl, thumbnailUrl, and videoId.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideoAndThumbnail(
            @RequestParam("video") MultipartFile video,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            HttpServletRequest request) {
        try {

            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Missing or invalid Authorization header");
            }
            String token = header.substring(7);
            String username = jwtUtil.extractUsername(token);

            String videoContentType = video.getContentType();
            if (videoContentType == null || !videoContentType.startsWith("video/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Only video files are allowed for the video parameter");
            }

            String thumbContentType = thumbnail.getContentType();
            if (thumbContentType == null || !thumbContentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Only image files are allowed for the thumbnail parameter");
            }

            String videoId = UUID.randomUUID().toString();

            String videoOriginalName = Objects.requireNonNull(video.getOriginalFilename());
            String thumbOriginalName = Objects.requireNonNull(thumbnail.getOriginalFilename());
            String videoExtension = videoOriginalName.substring(videoOriginalName.lastIndexOf('.'));
            String thumbExtension = thumbOriginalName.substring(thumbOriginalName.lastIndexOf('.'));

            Path userDir = BASE_UPLOAD_DIR.resolve(username).resolve(videoId);
            Files.createDirectories(userDir);

            String videoFileName = "video_" + videoId + videoExtension;
            String thumbnailFileName = "thumbnail-" + videoId + thumbExtension;

            Path videoPath = userDir.resolve(videoFileName);
            video.transferTo(videoPath);

            Path thumbnailPath = userDir.resolve(thumbnailFileName);
            thumbnail.transferTo(thumbnailPath);

            String videoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment("posts", username, videoId, videoFileName)
                    .toUriString();

            String thumbnailUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment("posts", username, videoId, thumbnailFileName)
                    .toUriString();

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("videoUrl", videoUrl);
            responseMap.put("thumbnailUrl", thumbnailUrl);
            responseMap.put("videoId", videoId);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseMap);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error uploading files: " + e.getMessage());
        }
    }

    @GetMapping("/{username}/{videoId}/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable("username") String username,
            @PathVariable("videoId") String videoId,
            @PathVariable("fileName") String fileName) {
        try {
            Path userDir = BASE_UPLOAD_DIR.resolve(username).resolve(videoId);
            Path filePath = userDir.resolve(fileName);
            if (!filePath.normalize().startsWith(userDir.normalize())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if ("video/quicktime".equals(contentType)) {

                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            contentType != null ? contentType : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
