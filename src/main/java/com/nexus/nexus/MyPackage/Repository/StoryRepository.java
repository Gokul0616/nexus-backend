package com.nexus.nexus.MyPackage.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexus.nexus.MyPackage.Entities.Story;

public interface StoryRepository extends JpaRepository<Story, Long> {

    // Query to find stories of the users that the current user follows
    List<Story> findByUserIdIn(List<String> userIds);
}