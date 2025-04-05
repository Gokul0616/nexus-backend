package com.nexus.nexus.MyPackage.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexus.MyPackage.Dto.VideoWatchDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Entities.VideoWatch;
import com.nexus.nexus.MyPackage.Services.VideoWatchService;

@RestController
@RequestMapping("/video")
public class VideoWatchController {

    @Autowired
    private VideoWatchService videoWatchService;

    @PostMapping("/watch")
    public ResponseEntity<?> trackWatchTime(@RequestBody VideoWatchDto dto, Authentication authentication) {

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserModal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserModal currentUser = (UserModal) principal;

        VideoWatch watch = new VideoWatch(dto.getVideoId(), dto.getWatchTime(), dto.isFullyWatched(), currentUser);
        videoWatchService.recordWatch(watch);

        return ResponseEntity.ok("Watch time recorded");
    }
}
