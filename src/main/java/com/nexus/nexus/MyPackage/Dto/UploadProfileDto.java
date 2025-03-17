package com.nexus.nexus.MyPackage.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadProfileDto {
    private String fullName;
    private String bio;
    private String location;
    private String profilePic;
}
