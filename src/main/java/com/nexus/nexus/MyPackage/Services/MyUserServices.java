package com.nexus.nexus.MyPackage.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.nexus.nexus.MyPackage.Configuration.JwtRequestUtil;
import com.nexus.nexus.MyPackage.Dto.AuthenticationDto;
import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyUserServices {
    private final UserRepository userRepository;
    private final JwtRequestUtil jwtUtil;

    public List<UserModal> getAllUser() {
        return userRepository.findAll();
    }

    public String registerUser(UserModal user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        UserModal usermodal = userRepository.save(user);
        return jwtUtil.generateToken(usermodal.getUsername());
    }

    public String authenticate(AuthenticationDto authDto) {

        Optional<UserModal> userModalOpt = userRepository.findByEmail(authDto.getEmail());
        if (!userModalOpt.isPresent()) {
            throw new BadCredentialsException("User not found with username: " + authDto.getEmail());
        }

        UserModal userModal = userModalOpt.get();
        System.out.println(authDto.getPassword().equals(userModal.getPassword()));

        if (!authDto.getPassword().equals(userModal.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        String token = jwtUtil.generateToken(userModal.getUsername());

        return token;
    }

    public UserModal updateProfile(UserModal user) {
        Optional<UserModal> userModalOpt = userRepository.findByUsername(user.getUsername());
        if (!userModalOpt.isPresent()) {
            throw new IllegalArgumentException("User not found with userId: " + user.getUserId());
        }
        UserModal userModal = userModalOpt.get();
        userModal.setFullName(user.getFullName());
        userModal.setBio(user.getBio());
        userModal.setLocation(user.getLocation());
        userModal.setProfilePic(user.getProfilePic());
        return userRepository.save(userModal);
    }

}
