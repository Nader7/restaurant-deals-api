package io.github.nader7.dealsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object used to store the deals data received from the restaurant deals api used as reference data
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DealData(
        String objectId,
        String discount,
        String dineIn,
        String lightning,
        String open,    //Handling incorrect key for start and end time
        String close,   //Remove once bug is fixed <Dependency EPIC ID or bug ID>
        String start,
        String end,
        String qtyLeft
) {}
