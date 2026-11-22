package io.github.nader7.dealsapi.model;

/**
 * Wrapper object used to store active deals before being transformed into the response DTO
 */
public record ActiveRestaurantDeal(
        Restaurant restaurant,
        Deal deal
) {}
