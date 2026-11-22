package io.github.nader7.dealsapi.model;

import java.time.LocalTime;

/**
 * Working object used to store and operate on deal data
 */
public record Deal(
        String objectId,
        String discount,
        String dineIn,
        String lightning,
        LocalTime start,
        LocalTime end,
        Integer qtyLeft
) {}
