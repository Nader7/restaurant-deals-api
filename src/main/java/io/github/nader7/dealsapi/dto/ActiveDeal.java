package io.github.nader7.dealsapi.dto;

/**
 * Data Transfer Object used to store all the active deal information returned by the /deals route
 */
public record ActiveDeal(
        String restaurantObjectId,
        String restaurantName,
        String restaurantAddress1,
        String restarantSuburb, //kept in small spelling mistake from the challenge document to match the contract in the PDF
        String restaurantOpen,
        String restaurantClose,
        String dealObjectId,
        String discount,
        String dineIn,
        String lightning,
        String qtyLeft
) {}
