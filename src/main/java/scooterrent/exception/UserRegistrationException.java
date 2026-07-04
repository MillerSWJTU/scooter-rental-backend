package scooterrent.exception;

import org.springframework.http.HttpStatus;

public class UserRegistrationException extends BusinessException {
    public UserRegistrationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
