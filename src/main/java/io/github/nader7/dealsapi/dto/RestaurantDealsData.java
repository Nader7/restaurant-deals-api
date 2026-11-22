package io.github.nader7.dealsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Data Transfer Object used to store all entries of RestaurantData
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RestaurantDealsData(
        List<RestaurantData> restaurants
) {}
