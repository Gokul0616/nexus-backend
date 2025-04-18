package com.nexus.nexus.MyPackage.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexus.nexus.MyPackage.Entities.Follow;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Repository.FollowRepository;
import com.nexus.nexus.MyPackage.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    private final UserRepository userRepository;

    @Transactional
    public boolean toggleFollow(String followerUserId, String followeeUserId) throws Exception {
        if (followRepository.existsByFollower_UserIdAndFollowee_UserId(followerUserId, followeeUserId)) {
            UserModal follower = userRepository.findByUserId(followerUserId)
                    .orElseThrow(() -> new Exception("Follower user not found"));
            UserModal followee = userRepository.findByUserId(followeeUserId)
                    .orElseThrow(() -> new Exception("Followee user not found"));
            Follow follow = followRepository.findByFollowerAndFollowee(follower, followee);
            if (follow != null) {
                followRepository.delete(follow);
            }
            return false;
        } else {

            UserModal follower = userRepository.findByUserId(followerUserId)
                    .orElseThrow(() -> new Exception("Follower user not found"));
            UserModal followee = userRepository.findByUserId(followeeUserId)
                    .orElseThrow(() -> new Exception("Followee user not found"));
            Follow follow = new Follow(follower, followee);
            followRepository.save(follow);
            return true;
        }
    }
}
