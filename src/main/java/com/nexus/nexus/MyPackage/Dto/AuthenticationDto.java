package com.nexus.nexus.MyPackage.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // Add this annotation
public class AuthenticationDto {
    private String login;
    private String password;
}
