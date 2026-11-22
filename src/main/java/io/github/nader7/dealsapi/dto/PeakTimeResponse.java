package io.github.nader7.dealsapi.dto;

/**
 * Data Transfer Object used to store the peak time data as a response for the /peak-time route
 */
public record PeakTimeResponse(
        String peakTimeStart,
        String peakTimeEnd
) {}
