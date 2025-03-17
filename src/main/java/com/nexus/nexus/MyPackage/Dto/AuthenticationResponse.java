package com.nexus.nexus.MyPackage.Dto;

public class AuthenticationResponse {
    private final String jwt;
    private final String message;

    public AuthenticationResponse(String jwt, String message) {
        this.jwt = jwt;
        this.message = message;
    }

    public String getJwt() {
        return jwt;
    }

    public String getMessage() {
        return message;
    }
}
