package com.nexus.nexus.MyPackage.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationDto {
    private String login;
    private String password;
}
