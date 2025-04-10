package com.nexus.nexus.MyPackage.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nexus.nexus.MyPackage.Entities.Follow;
import com.nexus.nexus.MyPackage.Entities.UserModal;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollower_UserIdAndFollowee_UserId(String followerUserId, String followeeUserId);

    boolean existsByFollowerAndFollowee(UserModal follower, UserModal followee);

    Follow findByFollowerAndFollowee(UserModal follower, UserModal followee);

    @Query("SELECT f.followee.userId FROM Follow f WHERE f.follower.userId = :userId")
    List<String> findFollowedUserIdsByUserId(@Param("userId") String userId);
}
