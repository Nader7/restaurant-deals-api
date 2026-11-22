package io.github.nader7.dealsapi.model;

import java.time.LocalTime;
import java.util.List;

/**
 * Working object used to store and operate on restaurant data
 */
public record Restaurant(
        String objectId,
        String name,
        String address1,
        String suburb,
        LocalTime open,
        LocalTime close,
        List<Deal> deals
) {}
