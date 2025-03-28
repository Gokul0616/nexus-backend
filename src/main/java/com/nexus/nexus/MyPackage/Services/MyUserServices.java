package com.nexus.nexus.MyPackage.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.nexus.nexus.MyPackage.Configuration.JwtRequestUtil;
import com.nexus.nexus.MyPackage.Dto.AuthenticationDto;
import com.nexus.nexus.MyPackage.Dto.OtherUserProfileDto;
import com.nexus.nexus.MyPackage.Dto.UploadProfileDto;
import com.nexus.nexus.MyPackage.Dto.UserProfileDto;
import com.nexus.nexus.MyPackage.Dto.UserSearchResultDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideoLike;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.FollowRepository;
import com.nexus.nexus.MyPackage.Repository.UserRepository;
import com.nexus.nexus.MyPackage.Repository.VideoLikeRepository;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;
import com.nexus.nexus.MyPackage.utils.Email.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyUserServices {
    private final UserRepository userRepository;
    private final JwtRequestUtil jwtUtil;
    private final VideoLikeRepository videoLikeRepository;
    private final VideosRepository videosRepository;
    private final FollowRepository followRepository;
    private final EmailService emailService;

    public List<UserModal> getAllUser() {
        return userRepository.findAll();
    }

    public String registerUser(UserModal user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        user.setUserId(UUID.randomUUID().toString());
        user.setCreatedAt(LocalDateTime.now());

        UserModal savedUser = userRepository.save(user);
        return jwtUtil.generateToken(savedUser.getUsername());
    }

    public String authenticate(AuthenticationDto authDto) {

        String login = authDto.getLogin();
        Optional<UserModal> userModalOpt;
        if (login.contains("@")) {
            userModalOpt = userRepository.findByEmail(login);
        } else {
            userModalOpt = userRepository.findByUsername(login);
        }

        if (!userModalOpt.isPresent()) {
            throw new BadCredentialsException("User not found with login: " + login);
        }

        UserModal userModal = userModalOpt.get();
        if (!authDto.getPassword().equals(userModal.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        String token = jwtUtil.generateToken(userModal.getUsername());
        return token;
    }

    public UserProfileDto getUserProfile(Authentication authentication) {
        UserModal authUser = (UserModal) authentication.getPrincipal();
        UserModal user = userRepository.findByIdWithFollows(authUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<VideosEntity> videos = videosRepository.findByUserId(user.getUserId());

        List<VideoLike> videoLikes = videoLikeRepository.findByUser(user);
        List<VideosEntity> likedVideos = videoLikes.stream()
                .map(VideoLike::getVideo)
                .collect(Collectors.toList());

        UserProfileDto userProfile = new UserProfileDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getBio(),
                user.getLocation(),
                user.getStreakPercentage(),
                user.getProfilePic(),
                String.valueOf(videos.size()),
                String.valueOf(user.getFollowerCount()),
                String.valueOf(user.getFollowingCount()),
                videos,
                likedVideos // send liked videos
        );
        return userProfile;
    }

    public ResponseEntity<OtherUserProfileDto> getOtherUserProfile(Authentication authentication, String username) {
        Optional<UserModal> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        UserModal user = userOptional.get();
        List<VideosEntity> videos = videosRepository.findByUserId(user.getUserId());

        boolean isFollowing = false;
        if (authentication != null && authentication.getPrincipal() instanceof UserModal) {
            UserModal currentUser = (UserModal) authentication.getPrincipal();
            isFollowing = followRepository.existsByFollower_UserIdAndFollowee_UserId(
                    currentUser.getUserId(), user.getUserId());
        }

        OtherUserProfileDto userProfile = new OtherUserProfileDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getBio(),
                user.getLocation(),
                user.getStreakPercentage(),
                user.getProfilePic(),
                String.valueOf(videos.size()),
                String.valueOf(user.getFollowerCount()),
                String.valueOf(user.getFollowingCount()),
                videos,
                isFollowing);
        return ResponseEntity.ok(userProfile);
    }

    public ResponseEntity<?> updateProfile(Authentication authentication, UploadProfileDto updateDto) {
        try {
            UserModal currentUser = (UserModal) authentication.getPrincipal();

            currentUser.setFullName(updateDto.getFullName());
            currentUser.setBio(updateDto.getBio());
            currentUser.setLocation(updateDto.getLocation());

            String newProfilePic = updateDto.getProfilePic();
            if (newProfilePic == null || newProfilePic.trim().isEmpty()) {
                newProfilePic = currentUser.getProfilePic();
            }
            currentUser.setProfilePic(newProfilePic);

            updateProfile(currentUser);
            return ResponseEntity.ok("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public UserModal updateProfile(UserModal user) {
        Optional<UserModal> userModalOpt = userRepository.findByUsername(user.getUsername());
        if (!userModalOpt.isPresent()) {
            throw new IllegalArgumentException("User not found with userId: " + user.getUserId());
        }
        UserModal userModal = userModalOpt.get();
        userModal.setFullName(user.getFullName());
        userModal.setBio(user.getBio());
        userModal.setLocation(user.getLocation());
        userModal.setProfilePic(user.getProfilePic());
        return userRepository.save(userModal);
    }

    public boolean existsByEmail(String email) {
        Optional<UserModal> userOpt = userRepository.findByEmail(email);
        return userOpt.isPresent();
    }

    public List<UserSearchResultDto> searchUsers(String query) {
        List<UserModal> users = userRepository.findByUsernameContainingIgnoreCase(query);
        return users.stream()
                .map(user -> new UserSearchResultDto(
                        user.getUserId(),
                        user.getUsername(),
                        user.getProfilePic()))
                .collect(Collectors.toList());
    }

    // Get username for a given userId
    public String getUsernameByUserId(String userId) {
        Optional<UserModal> user = userRepository.findByUserId(userId);
        return user != null ? user.get().getUsername() : "";
    }

    // Get profile picture URL for a given userId
    public String getProfilePicByUserId(String userId) {
        Optional<UserModal> user = userRepository.findByUserId(userId);
        return user != null ? user.get().getProfilePic() : "";
    }

    public void sendResetInstructions(String email) {
        String token = generateResetToken();
        Optional<UserModal> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UserModal user = userOpt.get();
            user.setResetToken(token);
            userRepository.save(user);
        }

        String resetLink = "https://nexusapp.com/reset-password?token=" + token;
        String subject = "Password Reset Request";

        String htmlContent = "<html>" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 0;\">" +
                "  <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                "    <tr>" +
                "      <td align=\"center\" style=\"padding: 20px;\">" +
                "        <table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;\">"
                +
                "          <tr>" +
                "            <td style=\"background-color: #0066cc; padding: 20px; text-align: center;\">" +
                "              <h1 style=\"margin: 0; color: #ffffff; font-size: 24px;\">Password Reset Request</h1>" +
                "            </td>" +
                "          </tr>" +
                "          <tr>" +
                "            <td style=\"padding: 20px; color: #333333;\">" +
                "              <p style=\"font-size: 16px; margin-bottom: 16px;\">Hello,</p>" +
                "              <p style=\"font-size: 16px; margin-bottom: 16px;\">We received a request to reset your password. Please click the button below to reset your password:</p>"
                +
                "              <p style=\"text-align: center; margin-bottom: 20px;\">" +
                "                <a href=\"" + resetLink
                + "\" style=\"display: inline-block; padding: 12px 24px; font-size: 16px; color: #ffffff; background-color: #0066cc; text-decoration: none; border-radius: 4px;\">"
                +
                "                  Reset Password" +
                "                </a>" +
                "              </p>" +
                "              <p style=\"font-size: 14px; color: #777777; margin-bottom: 16px;\">If you did not request a password reset, please ignore this email.</p>"
                +
                "              <p style=\"font-size: 16px; margin-bottom: 4px;\">Thank you,</p>" +
                "              <p style=\"font-size: 16px; margin: 0;\">Your Nexus Team</p>" +
                "            </td>" +
                "          </tr>" +
                "          <tr>" +
                "            <td style=\"background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; color: #777777;\">"
                +
                "              &copy; 2025 Nexus. All rights reserved." +
                "            </td>" +
                "          </tr>" +
                "        </table>" +
                "      </td>" +
                "    </tr>" +
                "  </table>" +
                "</body>" +
                "</html>";

        emailService.sendHtmlEmail(email, subject, htmlContent);
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

}
