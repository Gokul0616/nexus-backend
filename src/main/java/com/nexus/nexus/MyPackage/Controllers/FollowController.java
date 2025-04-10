package com.nexus.nexus.MyPackage.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexus.MyPackage.Dto.FollowRequest;
import com.nexus.nexus.MyPackage.Services.FollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/toggleFollow")
    public ResponseEntity<String> toggleFollow(@RequestBody FollowRequest followRequest) {
        try {
            boolean isNowFollowing = followService.toggleFollow(
                    followRequest.getFollowerId(),
                    followRequest.getFolloweeId());
            return ResponseEntity.ok(isNowFollowing ? "Following" : "Unfollowed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
