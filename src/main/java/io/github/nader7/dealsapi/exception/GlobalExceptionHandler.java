package io.github.nader7.dealsapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RestaurantDataUnavailableException.class)
    public ProblemDetail handleRestaurantDataUnavailable(RestaurantDataUnavailableException e) {
        log.error("Restaurant data unavailable", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "Restaurant data is unavailable");
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ProblemDetail handleInvalidTimeOfDay(DateTimeParseException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "The time of day must be in a 12h or 24 format such as 3:00pm or 15:00");
    }
}
