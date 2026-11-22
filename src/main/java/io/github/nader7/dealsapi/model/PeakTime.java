package io.github.nader7.dealsapi.model;

import java.time.LocalTime;

/**
 * Working object used to store and operate on deal peak time data
 */
public record PeakTime(
        LocalTime start,
        LocalTime end
) {}
