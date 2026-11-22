package io.github.nader7.dealsapi.exception;


public class RestaurantDataUnavailableException extends RuntimeException {

    public RestaurantDataUnavailableException(String message) {
        super(message);
    }

    public RestaurantDataUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
