package scooterrent.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String timestamp;
    private String path;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public ErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.timestamp = java.time.LocalDateTime.now().toString();
        this.path = path;
    }

    public ErrorResponse(int status, String message, String timestamp, String path) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }
} 