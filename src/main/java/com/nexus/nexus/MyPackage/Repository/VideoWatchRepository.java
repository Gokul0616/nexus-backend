package com.nexus.nexus.MyPackage.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideoWatch;

public interface VideoWatchRepository extends JpaRepository<VideoWatch, Long> {
    List<VideoWatch> findAllByUser(UserModal user);

    // Optionally, you could add a method to fetch watch records by videoId:
    List<VideoWatch> findAllByVideoId(String videoId);
}
