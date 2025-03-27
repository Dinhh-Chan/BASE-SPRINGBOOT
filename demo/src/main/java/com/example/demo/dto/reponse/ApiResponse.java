package com.example.demo.common.dto;

import java.time.LocalDateTime;

/**
 * Standard API response for all controller operations
 */
public class ApiResponse {
    private Boolean success;
    private String message;
    private LocalDateTime timestamp;

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}