package com.nexus.nexus.MyPackage.utils.profileImageUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {

    private final String BASE_DIRECTORY = "uploads/profileImage/";

    /**
     * Saves the provided image into a user-specific directory.
     *
     * @param image    the MultipartFile representing the image
     * @param username the username used to create the directory
     * @param fileName the desired name of the image file
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void saveImage(MultipartFile image, String username, String fileName) throws IOException {

        Path userDirPath = Paths.get(BASE_DIRECTORY, username);

        if (!Files.exists(userDirPath)) {
            Files.createDirectories(userDirPath);
        }

        Path filePath = userDirPath.resolve(fileName);

        Files.write(filePath, image.getBytes());
    }
}
