package com.nexus.nexus.MyPackage.utils.profileImageUpload;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageService {
    void saveImage(MultipartFile image, String username, String fileName) throws IOException;
}
