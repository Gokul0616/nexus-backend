package com.nexus.nexus.MyPackage.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nexus.nexus.MyPackage.Entities.Follow;
import com.nexus.nexus.MyPackage.Entities.UserModal;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollower_UserIdAndFollowee_UserId(String followerUserId, String followeeUserId);

    boolean existsByFollowerAndFollowee(UserModal follower, UserModal followee);

    Follow findByFollowerAndFollowee(UserModal follower, UserModal followee);
}
