package com.nexus.nexus.MyPackage.utils.profileImageUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nexus.nexus.MyPackage.Configuration.JwtRequestUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("images")
public class ImageController {
    private final JwtRequestUtil jwtUtil;
    private final ImageService imageService;
    private static final Path UPLOAD_DIR = Paths.get("uploads", "profileImage");

    static {
        try {
            Files.createDirectories(UPLOAD_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory", e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image,
            HttpServletRequest request) {
        try {

            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing or invalid Authorization header");
            }
            String token = header.substring(7);
            String username = jwtUtil.extractUsername(token);

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Only image files are allowed");
            }

            String originalFilename = Objects.requireNonNull(image.getOriginalFilename());
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String fileName = username + "_" + UUID.randomUUID() + extension;

            Path userDir = UPLOAD_DIR.resolve(username);
            Files.createDirectories(userDir);
            Path filePath = userDir.resolve(fileName);
            image.transferTo(filePath);

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment("images", username, fileName)
                    .toUriString();

            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image: " + e.getMessage());
        }
    }

    @GetMapping("/{username}/{fileName:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String username,
            @PathVariable String fileName) {
        try {
            Path userDir = UPLOAD_DIR.resolve(username);
            Path filePath = userDir.resolve(fileName);

            if (!filePath.normalize().startsWith(userDir.normalize())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}