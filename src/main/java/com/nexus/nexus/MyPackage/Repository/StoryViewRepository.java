package com.nexus.nexus.MyPackage.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nexus.nexus.MyPackage.Entities.StoryView;

@Repository
public interface StoryViewRepository extends JpaRepository<StoryView, Long> {
    List<StoryView> findByStoryId(Long storyId);
}
