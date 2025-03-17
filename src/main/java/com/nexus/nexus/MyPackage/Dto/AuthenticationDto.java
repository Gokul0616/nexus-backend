package com.nexus.nexus.MyPackage.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class AuthenticationDto {
    private String email;
    private String password;
}
