package com.nexus.nexus.MyPackage.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.nexus.MyPackage.Entities.VideoWatch;
import com.nexus.nexus.MyPackage.Repository.VideoWatchRepository;

@Service
public class VideoWatchService {

    @Autowired
    private VideoWatchRepository videoWatchRepository;

    /**
     * Saves a video watch record.
     */
    public VideoWatch recordWatch(VideoWatch watch) {
        return videoWatchRepository.save(watch);
    }
}
