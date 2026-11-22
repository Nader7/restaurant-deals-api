package io.github.nader7.dealsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Data Transfer Object used to store data received from the restaurant deals api used as reference data
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RestaurantData(
        String objectId,
        String name,
        String address1,
        String suburb,
        List<String> cuisines,
        String imageLink,
        String open,
        String close,
        List<DealData> deals
) {}
