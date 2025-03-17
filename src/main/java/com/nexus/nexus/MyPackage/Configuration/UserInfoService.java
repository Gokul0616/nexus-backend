package com.nexus.nexus.MyPackage.Configuration;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nexus.nexus.MyPackage.Entities.UserModal;
import com.nexus.nexus.MyPackage.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserInfoService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModal> userDetail = userRepo.findByUsername(username);
        if (userDetail.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return (UserDetails) userDetail.get();
    }

}
