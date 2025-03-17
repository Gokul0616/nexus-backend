package com.nexus.nexus.MyPackage.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexus.MyPackage.Configuration.JwtRequestUtil;
import com.nexus.nexus.MyPackage.Configuration.UserInfoService;
import com.nexus.nexus.MyPackage.Dto.AuthenticationDto;
import com.nexus.nexus.MyPackage.Dto.AuthenticationResponse;
import com.nexus.nexus.MyPackage.Dto.UploadProfileDto;
import com.nexus.nexus.MyPackage.Dto.UserProfileDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;
import com.nexus.nexus.MyPackage.Services.MyUserServices;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final MyUserServices myUserServices;
    private final JwtRequestUtil jwtRequestUtil;
    private final UserInfoService userInfoService;
    private final VideosRepository videosRepository;

    @GetMapping("/test")
    private String test() {
        return "test";
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<UserModal>> getUsers() {
        return ResponseEntity.ok().body(myUserServices.getAllUser());
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody AuthenticationDto authDto) {
        String token = myUserServices.authenticate(authDto);
        return ResponseEntity.ok(new AuthenticationResponse(token, "Login Successful"));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserModal user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            String token = myUserServices.registerUser(user);
            return ResponseEntity.ok(new AuthenticationResponse(token, "Signin Successful"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(Authentication authentication) {

        UserModal user = (UserModal) authentication.getPrincipal();
        System.out.println(user.getUserId());
        List<VideosEntity> videos = videosRepository.findByUserId(user.getUserId());

        UserProfileDto userProfile = new UserProfileDto(
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getBio(),
                user.getLocation(),
                user.getStreakPercentage(),
                user.getProfilePic(),
                String.valueOf(videos.size()),
                user.getFollowerCount(),
                user.getFollowingCount(),
                videos);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody UploadProfileDto updateDto,
            Authentication authentication) {
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

            myUserServices.updateProfile(currentUser);
            return ResponseEntity.ok("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/authenticate")
    public ResponseEntity<?> authenticateToken(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing or invalid");
        }

        String token = authHeader.substring(7);
        if (jwtRequestUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired");
        }

        String username = jwtRequestUtil.extractUsername(token);

        UserDetails userDetails = userInfoService.loadUserByUsername(username);
        if (jwtRequestUtil.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok("Token is valid for user: " + username);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

}
