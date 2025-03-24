package com.nexus.nexus.MyPackage.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nexus.nexus.MyPackage.Dto.FollowRequest;
import com.nexus.nexus.MyPackage.Services.FollowService;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

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
