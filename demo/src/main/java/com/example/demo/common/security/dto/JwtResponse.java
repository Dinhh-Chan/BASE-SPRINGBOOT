package com.example.demo.common.security.dto;

import java.util.Date;

public class JwtResponse {
    private String token;
    private String type;
    private String username;
    private Date expiresAt;

    public JwtResponse(String token, String type, String username, Date expiresAt) {
        this.token = token;
        this.type = type;
        this.username = username;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}