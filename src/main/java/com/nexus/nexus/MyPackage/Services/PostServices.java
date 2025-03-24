package com.nexus.nexus.MyPackage.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nexus.nexus.MyPackage.Entities.VideosEntity;
import com.nexus.nexus.MyPackage.Repository.VideosRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServices {
    private final VideosRepository videosRepository;

    public List<VideosEntity> getRecommendation() {
        return videosRepository.findAll();

    }
}